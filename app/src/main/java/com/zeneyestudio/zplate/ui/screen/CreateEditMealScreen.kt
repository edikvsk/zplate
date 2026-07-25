package com.zeneyestudio.zplate.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zeneyestudio.zplate.data.model.MealNutrition
import com.zeneyestudio.zplate.data.model.Product
import com.zeneyestudio.zplate.data.remote.OpenFoodFactsClient
import com.zeneyestudio.zplate.ui.components.MealArtwork
import com.zeneyestudio.zplate.ui.components.WellnessBackdrop
import com.zeneyestudio.zplate.ui.components.bouncyClick
import com.zeneyestudio.zplate.ui.components.mealVisual
import java.util.Locale
import kotlinx.coroutines.delay

data class ProductWithWeightUI(
    val product: Product,
    val weightGrams: Int
)

@Composable
fun CreateEditMealScreen(
    mealName: String,
    products: List<ProductWithWeightUI>,
    nutrition: MealNutrition,
    mealType: String,
    ambientMealType: String,
    allProducts: List<Product>,
    suggestedProductWeight: (Product) -> Int,
    onNameChange: (String) -> Unit,
    onMealTypeChange: (String) -> Unit,
    onRemoveProduct: (Int) -> Unit,
    onAddProduct: (Product, Int) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit = {}
) {
    var productDialogOpen by remember { mutableStateOf(false) }
    val visual = mealVisual(ambientMealType)
    val onAccent = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.background else Color.White

    if (productDialogOpen) {
        ProductPicker(
            products = allProducts,
            accent = visual.accent,
            suggestedProductWeight = suggestedProductWeight,
            onPicked = { product, weight ->
                onAddProduct(product, weight)
                productDialogOpen = false
            },
            onDismiss = { productDialogOpen = false }
        )
    }

    WellnessBackdrop(
        accent = visual.accent,
        topColor = visual.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .size(44.dp)
                        .bouncyClick(onClick = onBack),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = .96f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Rounded.ArrowBack,
                            contentDescription = "Назад",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = .72f)
                        )
                    }
                }
                Spacer(Modifier.size(14.dp))
                Text(
                    text = if (mealName.isBlank()) "Новое блюдо" else "Изменить блюдо",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = .78f)
                )
                MealArtwork(mealType, 66.dp)
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = mealName,
                    onValueChange = onNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Название") },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    colors = softFieldColors(visual.accent)
                )

                Spacer(Modifier.height(14.dp))
                MealTypeSelector(
                    selected = mealType,
                    accent = visual.accent,
                    onSelected = onMealTypeChange
                )

                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Состав",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = .78f)
                    )
                    Surface(
                        modifier = Modifier
                            .height(42.dp)
                            .bouncyClick { productDialogOpen = true },
                        shape = RoundedCornerShape(16.dp),
                        color = visual.accent.copy(alpha = .13f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Rounded.Add,
                                contentDescription = null,
                                tint = visual.accent,
                                modifier = Modifier.size(19.dp)
                            )
                            Spacer(Modifier.size(5.dp))
                            Text(
                                "Добавить",
                                style = MaterialTheme.typography.labelLarge,
                                color = visual.accent
                            )
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))

                if (products.isEmpty()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(104.dp)
                            .bouncyClick { productDialogOpen = true },
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = .96f)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = null, tint = visual.accent)
                            Spacer(Modifier.size(8.dp))
                            Text(
                                "Выбрать продукты",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        products.forEachIndexed { index, item ->
                            ProductRow(item = item, onRemove = { onRemoveProduct(index) })
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))
                NutritionSummary(nutrition = nutrition, accent = visual.accent)
                Spacer(Modifier.height(18.dp))
            }

            val canSave = mealName.isNotBlank() && products.isNotEmpty()
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .bouncyClick(enabled = canSave, onClick = onSave),
                shape = RoundedCornerShape(20.dp),
                color = if (canSave) visual.accent else MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = if (canSave) 7.dp else 0.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "Сохранить",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (canSave) onAccent else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun MealTypeSelector(
    selected: String,
    accent: Color,
    onSelected: (String) -> Unit
) {
    val onAccent = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.background else Color.White
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = .96f)
    ) {
        Row(
            modifier = Modifier.padding(5.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("завтрак", "обед", "ужин").forEach { type ->
                val active = selected == type
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .bouncyClick { onSelected(type) },
                    shape = RoundedCornerShape(17.dp),
                    color = if (active) accent else Color.Transparent
                ) {
                    Text(
                        text = type.replaceFirstChar { it.titlecase(Locale.getDefault()) },
                        modifier = Modifier.padding(vertical = 10.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (active) onAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductRow(item: ProductWithWeightUI, onRemove: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = .97f)
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 10.dp, bottom = 10.dp, end = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = .8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = "${item.weightGrams} г  ·  ${
                        (item.product.caloriesPer100g * item.weightGrams / 100f).toInt()
                    } ккал",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Rounded.DeleteOutline,
                    contentDescription = "Убрать",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun NutritionSummary(nutrition: MealNutrition, accent: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = accent.copy(alpha = .11f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NutritionValue("${nutrition.calories}", "ккал", accent)
            NutritionValue("${nutrition.protein.toInt()}", "белки", accent)
            NutritionValue("${nutrition.fat.toInt()}", "жиры", accent)
            NutritionValue("${nutrition.carbs.toInt()}", "углев.", accent)
        }
    }
}

@Composable
private fun NutritionValue(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, color = color)
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProductPicker(
    products: List<Product>,
    accent: Color,
    suggestedProductWeight: (Product) -> Int,
    onPicked: (Product, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val onAccent = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.background else Color.White
    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<Product?>(null) }
    var weight by remember { mutableStateOf("100") }
    var onlineProducts by remember { mutableStateOf(emptyList<Product>()) }
    var onlineLoading by remember { mutableStateOf(false) }
    var onlineSearchFailed by remember { mutableStateOf(false) }
    val selectProduct: (Product) -> Unit = { product ->
        selected = product
        weight = suggestedProductWeight(product).coerceAtLeast(1).toString()
    }

    LaunchedEffect(query, selected) {
        val normalizedQuery = query.trim()
        if (selected != null || normalizedQuery.length < 2) {
            onlineProducts = emptyList()
            onlineLoading = false
            onlineSearchFailed = false
            return@LaunchedEffect
        }
        delay(450)
        onlineLoading = true
        onlineSearchFailed = false
        runCatching {
            OpenFoodFactsClient.searchProducts(normalizedQuery)
        }.onSuccess { result ->
            onlineProducts = result.filter { remote ->
                products.none { local ->
                    local.name.equals(remote.name, ignoreCase = true)
                }
            }
        }.onFailure {
            onlineProducts = emptyList()
            onlineSearchFailed = true
        }
        onlineLoading = false
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(.94f)
                .fillMaxHeight(.78f),
            shape = RoundedCornerShape(30.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (selected == null) "Продукты" else selected!!.name,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Rounded.Close, contentDescription = "Закрыть")
                    }
                }
                Spacer(Modifier.height(12.dp))

                if (selected == null) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Поиск") },
                        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        colors = softFieldColors(accent)
                    )
                    if (query.trim().length < 2) {
                        Text(
                            text = "Введите минимум 2 символа — подключится онлайн-каталог",
                            modifier = Modifier.padding(start = 4.dp, top = 8.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    val filtered = products.filter {
                        it.name.contains(query.trim(), ignoreCase = true)
                    }
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (query.trim().length >= 2) {
                            item(key = "online-status") {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Онлайн",
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = accent
                                    )
                                    if (onlineLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp,
                                            color = accent
                                        )
                                    } else if (onlineSearchFailed) {
                                        Text(
                                            text = "не удалось загрузить",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    } else if (onlineProducts.isEmpty()) {
                                        Text(
                                            text = "ничего не найдено",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        items(
                            onlineProducts,
                            key = { "online-${it.name.lowercase()}" }
                        ) { product ->
                            ProductSearchRow(
                                product = product,
                                accent = accent,
                                source = "онлайн",
                                onClick = { selectProduct(product) }
                            )
                        }

                        if (filtered.isNotEmpty()) {
                            item(key = "local-status") {
                                Text(
                                    text = "На устройстве",
                                    modifier = Modifier.padding(
                                        horizontal = 4.dp,
                                        vertical = 6.dp
                                    ),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        items(filtered, key = { "local-${it.id}" }) { product ->
                            ProductSearchRow(
                                product = product,
                                accent = accent,
                                source = null,
                                onClick = { selectProduct(product) }
                            )
                        }
                    }
                } else {
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "Сколько граммов?",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { if (it.all(Char::isDigit)) weight = it },
                        modifier = Modifier
                            .width(190.dp)
                            .align(Alignment.CenterHorizontally),
                        suffix = {
                            Text(
                                "грамм",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        textStyle = MaterialTheme.typography.headlineMedium.copy(
                            textAlign = androidx.compose.ui.text.style.TextAlign.End
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        colors = softFieldColors(accent)
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        listOf(50, 100, 150, 200).forEach { amount ->
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .bouncyClick { weight = amount.toString() },
                                shape = RoundedCornerShape(14.dp),
                                color = if (weight == amount.toString()) {
                                    accent.copy(alpha = .16f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .5f)
                                }
                            ) {
                                Text(
                                    "$amount",
                                    modifier = Modifier.padding(vertical = 9.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    color = if (weight == amount.toString()) accent
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .bouncyClick(
                                enabled = (weight.toIntOrNull() ?: 0) > 0
                            ) {
                                onPicked(selected!!, weight.toInt())
                            },
                        shape = RoundedCornerShape(19.dp),
                        color = accent
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("Добавить", color = onAccent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductSearchRow(
    product: Product,
    accent: Color,
    source: String?,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .bouncyClick(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .45f)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = buildString {
                        append("${product.caloriesPer100g} ккал / 100 г")
                        source?.let { append("  ·  $it") }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Rounded.Add,
                contentDescription = "Выбрать",
                tint = accent
            )
        }
    }
}

@Composable
private fun softFieldColors(accent: Color) = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = .94f),
    focusedBorderColor = accent.copy(alpha = .55f),
    unfocusedBorderColor = Color.Transparent
)
