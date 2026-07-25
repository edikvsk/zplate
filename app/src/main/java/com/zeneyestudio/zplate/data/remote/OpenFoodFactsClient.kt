package com.zeneyestudio.zplate.data.remote

import com.zeneyestudio.zplate.data.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlin.math.roundToInt

object OpenFoodFactsClient {
    private const val BASE_URL = "https://search.openfoodfacts.org/search"

    suspend fun searchProducts(query: String): List<Product> = withContext(Dispatchers.IO) {
        val encodedQuery = URLEncoder.encode(query.trim(), Charsets.UTF_8.name())
        val url = URL(
            "$BASE_URL?q=$encodedQuery&langs=ru,en&page_size=20" +
                "&fields=code,product_name,brands,nutriments"
        )
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 7_000
            readTimeout = 7_000
            setRequestProperty(
                "User-Agent",
                "MyDnevnik/1.0 (Android; Open Food Facts product search)"
            )
            setRequestProperty("Accept", "application/json")
        }

        try {
            if (connection.responseCode !in 200..299) {
                error("Open Food Facts returned ${connection.responseCode}")
            }
            val response = InputStreamReader(
                connection.inputStream,
                Charsets.UTF_8
            ).buffered().use { it.readText() }
            val root = JSONObject(response)
            val products = root.optJSONArray("hits") ?: return@withContext emptyList()
            buildList {
                for (index in 0 until products.length()) {
                    val item = products.optJSONObject(index) ?: continue
                    val name = item.optString("product_name").trim()
                    if (name.isBlank()) continue

                    val nutrients = item.optJSONObject("nutriments") ?: continue
                    val calories = nutrients.number("energy-kcal_100g")
                        ?: nutrients.number("energy-kj_100g")?.div(4.184)
                        ?: continue
                    val protein = nutrients.number("proteins_100g") ?: 0.0
                    val fat = nutrients.number("fat_100g") ?: 0.0
                    val carbs = nutrients.number("carbohydrates_100g") ?: 0.0
                    val brand = item.optJSONArray("brands")
                        ?.firstString()
                        .orEmpty()
                        .ifBlank {
                            item.optString("brands").substringBefore(",").trim()
                        }

                    add(
                        Product(
                            name = if (brand.isNotBlank() &&
                                !name.contains(brand, ignoreCase = true)
                            ) {
                                "$name · $brand"
                            } else {
                                name
                            },
                            caloriesPer100g = calories.roundToInt().coerceAtLeast(0),
                            proteinPer100g = protein.toFloat().coerceAtLeast(0f),
                            fatPer100g = fat.toFloat().coerceAtLeast(0f),
                            carbsPer100g = carbs.toFloat().coerceAtLeast(0f),
                            isDefault = false,
                            category = "Open Food Facts"
                        )
                    )
                }
            }.distinctBy { it.name.lowercase() }
        } finally {
            connection.disconnect()
        }
    }

    private fun JSONObject.number(key: String): Double? {
        if (!has(key) || isNull(key)) return null
        return when (val value = opt(key)) {
            is Number -> value.toDouble()
            is String -> value.replace(',', '.').toDoubleOrNull()
            else -> null
        }
    }

    private fun JSONArray.firstString(): String =
        if (length() > 0) optString(0).trim() else ""
}
