package com.zeneyestudio.zplate

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.zeneyestudio.zplate.data.model.*
import com.zeneyestudio.zplate.data.repository.DailyLogRepository
import com.zeneyestudio.zplate.data.repository.MealRepository
import com.zeneyestudio.zplate.data.repository.ProductRepository
import com.zeneyestudio.zplate.ui.navigation.AppNavigation
import com.zeneyestudio.zplate.ui.theme.MyDnevnikTheme
import com.zeneyestudio.zplate.util.DefaultProducts
import com.zeneyestudio.zplate.util.TimeHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var productRepository: ProductRepository

    @Inject
    lateinit var mealRepository: MealRepository

    @Inject
    lateinit var dailyLogRepository: DailyLogRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyDnevnikTheme {
                MainApp(
                    productRepository = productRepository,
                    mealRepository = mealRepository,
                    dailyLogRepository = dailyLogRepository
                )
            }
        }
    }
}

@Composable
fun MainApp(
    productRepository: ProductRepository,
    mealRepository: MealRepository,
    dailyLogRepository: DailyLogRepository
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val preferences = remember(context) {
        context.getSharedPreferences("my_dnevnik_settings", android.content.Context.MODE_PRIVATE)
    }

    // State
    var isOnboardingComplete by remember(preferences) {
        mutableStateOf(preferences.getBoolean("onboarding_complete", false))
    }
    var breakfastRange by remember(preferences) {
        mutableStateOf(
            TimeHelper.TimeRange(
                preferences.getInt("breakfast_start_hour", 7),
                preferences.getInt("breakfast_start_minute", 0),
                preferences.getInt("breakfast_end_hour", 12),
                preferences.getInt("breakfast_end_minute", 0)
            )
        )
    }
    var lunchRange by remember(preferences) {
        mutableStateOf(
            TimeHelper.TimeRange(
                preferences.getInt("lunch_start_hour", 12),
                preferences.getInt("lunch_start_minute", 0),
                preferences.getInt("lunch_end_hour", 16),
                preferences.getInt("lunch_end_minute", 0)
            )
        )
    }
    var dinnerRange by remember(preferences) {
        mutableStateOf(
            TimeHelper.TimeRange(
                preferences.getInt("dinner_start_hour", 16),
                preferences.getInt("dinner_start_minute", 0),
                preferences.getInt("dinner_end_hour", 22),
                preferences.getInt("dinner_end_minute", 0)
            )
        )
    }

    fun persistTimeRanges(
        breakfast: TimeHelper.TimeRange,
        lunch: TimeHelper.TimeRange,
        dinner: TimeHelper.TimeRange
    ) {
        preferences.edit()
            .putInt("breakfast_start_hour", breakfast.startHour)
            .putInt("breakfast_start_minute", breakfast.startMinute)
            .putInt("breakfast_end_hour", breakfast.endHour)
            .putInt("breakfast_end_minute", breakfast.endMinute)
            .putInt("lunch_start_hour", lunch.startHour)
            .putInt("lunch_start_minute", lunch.startMinute)
            .putInt("lunch_end_hour", lunch.endHour)
            .putInt("lunch_end_minute", lunch.endMinute)
            .putInt("dinner_start_hour", dinner.startHour)
            .putInt("dinner_start_minute", dinner.startMinute)
            .putInt("dinner_end_hour", dinner.endHour)
            .putInt("dinner_end_minute", dinner.endMinute)
            .apply()
    }

    fun persistGoals(calories: Int, protein: Int, fat: Int, carbs: Int) {
        preferences.edit()
            .putInt("goal_calories", calories)
            .putInt("goal_protein", protein)
            .putInt("goal_fat", fat)
            .putInt("goal_carbs", carbs)
            .apply()
    }

    var dailyCalories by remember { mutableIntStateOf(0) }
    var dailyProtein by remember { mutableFloatStateOf(0f) }
    var dailyFat by remember { mutableFloatStateOf(0f) }
    var dailyCarbs by remember { mutableFloatStateOf(0f) }

    var goalCalories by remember(preferences) {
        mutableIntStateOf(preferences.getInt("goal_calories", 2000))
    }
    var goalProtein by remember(preferences) {
        mutableIntStateOf(preferences.getInt("goal_protein", 80))
    }
    var goalFat by remember(preferences) {
        mutableIntStateOf(preferences.getInt("goal_fat", 65))
    }
    var goalCarbs by remember(preferences) {
        mutableIntStateOf(preferences.getInt("goal_carbs", 250))
    }
    var recentMealIds by remember(preferences) {
        mutableStateOf(preferences.readMealIds("recent_meal_ids"))
    }
    var favoriteMealIds by remember(preferences) {
        mutableStateOf(preferences.readMealIds("favorite_meal_ids").toSet())
    }

    // Data
    val products by productRepository.getAllProducts().collectAsState(initial = emptyList())
    val meals by mealRepository.getAllMeals().collectAsState(initial = emptyList())
    val todayLogs by dailyLogRepository.getLogsByDate(dailyLogRepository.getTodayDate()).collectAsState(initial = emptyList())
    val historyStart = remember { LocalDate.now().minusDays(29) }
    val historyEnd = remember { LocalDate.now() }
    val historyLogs by dailyLogRepository.getLogsBetweenDates(
        dailyLogRepository.getDateString(historyStart),
        dailyLogRepository.getDateString(historyEnd)
    ).collectAsState(initial = emptyList())
    val yesterdayDate = remember {
        dailyLogRepository.getDateString(LocalDate.now().minusDays(1))
    }
    val yesterdayLogs = remember(historyLogs, yesterdayDate) {
        historyLogs.filter { it.date == yesterdayDate }
    }

    // Calculate meal nutritions
    var mealNutritions by remember { mutableStateOf<Map<Long, MealNutrition>>(emptyMap()) }
    var mealProductsMap by remember { mutableStateOf<Map<Long, List<MealProduct>>>(emptyMap()) }
    var mealsRefreshKey by remember { mutableIntStateOf(0) }
    val dailyCaloriesByDate = remember(historyLogs, mealNutritions) {
        historyLogs
            .groupBy { it.date }
            .mapValues { (_, logs) ->
                logs.sumOf { log -> mealNutritions[log.mealId]?.calories ?: 0 }
            }
    }

    // Load meal products and calculate nutrition
    LaunchedEffect(meals, mealsRefreshKey) {
        val nutritions = mutableMapOf<Long, MealNutrition>()
        val productsMap = mutableMapOf<Long, List<MealProduct>>()

        for (meal in meals) {
            val mealProducts = mealRepository.getMealProducts(meal.id).first()
            productsMap[meal.id] = mealProducts

            var totalCalories = 0
            var totalProtein = 0f
            var totalFat = 0f
            var totalCarbs = 0f

            for (mp in mealProducts) {
                val product = products.find { it.id == mp.productId }
                if (product != null) {
                    val multiplier = mp.weightGrams / 100f
                    totalCalories += (product.caloriesPer100g * multiplier).toInt()
                    totalProtein += product.proteinPer100g * multiplier
                    totalFat += product.fatPer100g * multiplier
                    totalCarbs += product.carbsPer100g * multiplier
                }
            }

            nutritions[meal.id] = MealNutrition(totalCalories, totalProtein, totalFat, totalCarbs)
        }

        mealProductsMap = productsMap
        mealNutritions = nutritions
    }

    // Calculate daily totals
    LaunchedEffect(todayLogs, mealNutritions) {
        var totalCal = 0
        var totalProtein = 0f
        var totalFat = 0f
        var totalCarbs = 0f

        for (log in todayLogs) {
            val nutrition = mealNutritions[log.mealId]
            if (nutrition != null) {
                totalCal += nutrition.calories
                totalProtein += nutrition.protein
                totalFat += nutrition.fat
                totalCarbs += nutrition.carbs
            }
        }

        dailyCalories = totalCal
        dailyProtein = totalProtein
        dailyFat = totalFat
        dailyCarbs = totalCarbs
    }

    val currentMealType = remember(breakfastRange, lunchRange, dinnerRange) {
        TimeHelper.getCurrentMealType(breakfastRange, lunchRange, dinnerRange)
    }

    // Debug: override meal type for testing
    var debugMealType by remember { mutableStateOf<String?>(null) }
    val displayMealType = debugMealType ?: currentMealType

    // Initialize default products
    LaunchedEffect(Unit) {
        if (productRepository.getProductCount() == 0) {
            productRepository.insertProducts(DefaultProducts.getDefaultProducts())
        }
        if (!preferences.getBoolean("test_meals_seeded", false)) {
            val availableProducts = productRepository.getAllProducts().first()
            val existingMeals = mealRepository.getAllMeals().first()
            val templates = listOf(
                Triple(
                    "Тестовый завтрак",
                    "завтрак",
                    listOf("Овсянка на молоке" to 250, "Банан" to 100)
                ),
                Triple(
                    "Тестовый обед",
                    "обед",
                    listOf("Куриная грудка" to 150, "Рис варёный" to 180, "Огурец" to 100)
                ),
                Triple(
                    "Тестовый ужин",
                    "ужин",
                    listOf("Лосось" to 160, "Капуста брокколи" to 180)
                )
            )

            templates.forEach { (name, type, ingredients) ->
                if (existingMeals.none { it.name == name }) {
                    val mealId = mealRepository.insertMeal(Meal(name = name, mealType = type))
                    val mealProducts = ingredients.mapNotNull { (productName, weight) ->
                        availableProducts.find { it.name == productName }?.let { product ->
                            MealProduct(
                                mealId = mealId,
                                productId = product.id,
                                weightGrams = weight
                            )
                        }
                    }
                    mealRepository.insertMealProducts(mealProducts)
                }
            }
            preferences.edit().putBoolean("test_meals_seeded", true).apply()
            mealsRefreshKey++
        }
    }

    // Main UI - no bottom navigation, just the content
    AppNavigation(
        navController = navController,
        modifier = Modifier.fillMaxSize(),
        isOnboardingComplete = isOnboardingComplete,
        currentMealType = displayMealType,
        onDebugMealTypeChange = { debugMealType = it },
        dailyCalories = dailyCalories,
        dailyCaloriesByDate = dailyCaloriesByDate,
        dailyProtein = dailyProtein,
        dailyFat = dailyFat,
        dailyCarbs = dailyCarbs,
        goalCalories = goalCalories,
        goalProtein = goalProtein,
        goalFat = goalFat,
        goalCarbs = goalCarbs,
        todayLogs = todayLogs,
        historyLogs = historyLogs,
        yesterdayLogs = yesterdayLogs,
        allMeals = meals,
        recentMealIds = recentMealIds,
        favoriteMealIds = favoriteMealIds,
        mealNutritions = mealNutritions,
        mealProductsMap = mealProductsMap,
        allProducts = products,
        breakfastRange = breakfastRange,
        lunchRange = lunchRange,
        dinnerRange = dinnerRange,
        onCompleteOnboarding = { b, l, d, cal, pro, fat, carbs ->
            isOnboardingComplete = true
            breakfastRange = b
            lunchRange = l
            dinnerRange = d
            goalCalories = cal
            goalProtein = pro
            goalFat = fat
            goalCarbs = carbs
            persistTimeRanges(b, l, d)
            persistGoals(cal, pro, fat, carbs)
            preferences.edit().putBoolean("onboarding_complete", true).apply()
        },
        onAddMealToDiary = { mealId, mealType ->
            recentMealIds = (listOf(mealId) + recentMealIds.filterNot { it == mealId })
                .take(8)
            preferences.writeMealIds("recent_meal_ids", recentMealIds)
            scope.launch {
                val today = dailyLogRepository.getTodayDate()
                dailyLogRepository.insertLog(
                    DailyLog(
                        date = today,
                        mealType = mealType,
                        mealId = mealId,
                        timestamp = System.currentTimeMillis()
                    )
                )
                mealsRefreshKey++
            }
        },
        onDeleteLog = { log ->
            scope.launch {
                dailyLogRepository.deleteLog(log)
                mealsRefreshKey++
            }
        },
        onCreateMeal = { name, type, productsWithWeights ->
            scope.launch {
                val mealId = mealRepository.insertMeal(
                    Meal(name = name, mealType = type)
                )
                val importedProductIds = mutableMapOf<String, Long>()
                val mealProducts = productsWithWeights.map { (product, weight) ->
                    val key = product.name.lowercase()
                    val productId = if (product.id != 0L) {
                        product.id
                    } else {
                        importedProductIds[key]
                            ?: products.find { it.name.equals(product.name, ignoreCase = true) }?.id
                            ?: productRepository.insertProduct(product)
                    }
                    importedProductIds[key] = productId
                    MealProduct(
                        mealId = mealId,
                        productId = productId,
                        weightGrams = weight
                    )
                }
                mealRepository.insertMealProducts(mealProducts)
                mealsRefreshKey++
            }
        },
        onUpdateMeal = { mealId, name, type, productsWithWeights ->
            scope.launch {
                mealRepository.updateMeal(
                    Meal(id = mealId, name = name, mealType = type)
                )
                mealRepository.deleteAllMealProducts(mealId)
                val importedProductIds = mutableMapOf<String, Long>()
                val mealProducts = productsWithWeights.map { (product, weight) ->
                    val key = product.name.lowercase()
                    val productId = if (product.id != 0L) {
                        product.id
                    } else {
                        importedProductIds[key]
                            ?: products.find { it.name.equals(product.name, ignoreCase = true) }?.id
                            ?: productRepository.insertProduct(product)
                    }
                    importedProductIds[key] = productId
                    MealProduct(
                        mealId = mealId,
                        productId = productId,
                        weightGrams = weight
                    )
                }
                mealRepository.insertMealProducts(mealProducts)
                mealsRefreshKey++
            }
        },
        onDeleteMeal = { mealId ->
            recentMealIds = recentMealIds.filterNot { it == mealId }
            favoriteMealIds = favoriteMealIds - mealId
            preferences.writeMealIds("recent_meal_ids", recentMealIds)
            preferences.writeMealIds("favorite_meal_ids", favoriteMealIds.toList())
            scope.launch {
                mealRepository.deleteMealById(mealId)
                mealsRefreshKey++
            }
        },
        onToggleFavoriteMeal = { mealId ->
            favoriteMealIds = if (mealId in favoriteMealIds) {
                favoriteMealIds - mealId
            } else {
                favoriteMealIds + mealId
            }
            preferences.writeMealIds("favorite_meal_ids", favoriteMealIds.toList())
        },
        suggestedProductWeight = { product ->
            preferences.getInt(productWeightKey(product), 100).coerceAtLeast(1)
        },
        onProductWeightUsed = { product, weight ->
            preferences.edit()
                .putInt(productWeightKey(product), weight.coerceAtLeast(1))
                .apply()
        },
        onCreateProduct = { product ->
            scope.launch {
                productRepository.insertProduct(product)
            }
        },
        onUpdateGoals = { cal, pro, fat, carbs ->
            goalCalories = cal
            goalProtein = pro
            goalFat = fat
            goalCarbs = carbs
            persistGoals(cal, pro, fat, carbs)
        },
        onUpdateTimeRanges = { b, l, d ->
            breakfastRange = b
            lunchRange = l
            dinnerRange = d
            persistTimeRanges(b, l, d)
        },
        onExportCsv = { },
        onImportCsv = { },
        onClearData = { }
    )
}

private fun SharedPreferences.readMealIds(key: String): List<Long> =
    getString(key, null)
        .orEmpty()
        .split(',')
        .mapNotNull { it.toLongOrNull() }
        .distinct()

private fun SharedPreferences.writeMealIds(key: String, ids: List<Long>) {
    edit().putString(key, ids.distinct().joinToString(",")).apply()
}

private fun productWeightKey(product: Product): String {
    val normalizedName = product.name.trim().lowercase(Locale.ROOT)
    return "last_product_weight_${normalizedName.hashCode()}"
}
