package com.zeneyestudio.zplate.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.zeneyestudio.zplate.data.model.*
import com.zeneyestudio.zplate.ui.screen.*
import com.zeneyestudio.zplate.util.TimeHelper

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    isOnboardingComplete: Boolean,
    currentMealType: String,
    onDebugMealTypeChange: (String) -> Unit = {},
    dailyCalories: Int,
    dailyCaloriesByDate: Map<String, Int>,
    dailyProtein: Float,
    dailyFat: Float,
    dailyCarbs: Float,
    goalCalories: Int,
    goalProtein: Int,
    goalFat: Int,
    goalCarbs: Int,
    todayLogs: List<DailyLog>,
    yesterdayLogs: List<DailyLog>,
    allMeals: List<Meal>,
    recentMealIds: List<Long>,
    favoriteMealIds: Set<Long>,
    mealNutritions: Map<Long, MealNutrition>,
    mealProductsMap: Map<Long, List<MealProduct>>,
    allProducts: List<Product>,
    breakfastRange: TimeHelper.TimeRange,
    lunchRange: TimeHelper.TimeRange,
    dinnerRange: TimeHelper.TimeRange,
    onCompleteOnboarding: (TimeHelper.TimeRange, TimeHelper.TimeRange, TimeHelper.TimeRange) -> Unit,
    onAddMealToDiary: (Long, String) -> Unit,
    onDeleteLog: (DailyLog) -> Unit,
    onCreateMeal: (String, String, List<Pair<Product, Int>>) -> Unit,
    onUpdateMeal: (Long, String, String, List<Pair<Product, Int>>) -> Unit,
    onDeleteMeal: (Long) -> Unit,
    onToggleFavoriteMeal: (Long) -> Unit,
    suggestedProductWeight: (Product) -> Int,
    onProductWeightUsed: (Product, Int) -> Unit,
    onCreateProduct: (Product) -> Unit,
    onUpdateGoals: (Int, Int, Int, Int) -> Unit,
    onUpdateTimeRanges: (TimeHelper.TimeRange, TimeHelper.TimeRange, TimeHelper.TimeRange) -> Unit,
    onExportCsv: () -> Unit,
    onImportCsv: () -> Unit,
    onClearData: () -> Unit
) {
    val startDestination = if (isOnboardingComplete) "home" else "onboarding"

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Onboarding
        composable("onboarding") {
            OnboardingScreen(
                onComplete = { breakfast, lunch, dinner ->
                    onCompleteOnboarding(breakfast, lunch, dinner)
                    navController.navigate("home") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        // Main home screen - circle progress + add button
        composable("home") {
            HomeScreen(
                currentCalories = dailyCalories,
                dailyCaloriesByDate = dailyCaloriesByDate,
                goalCalories = goalCalories,
                currentProtein = dailyProtein,
                currentFat = dailyFat,
                currentCarbs = dailyCarbs,
                todayLogs = todayLogs,
                meals = allMeals,
                mealNutritions = mealNutritions,
                goalProtein = goalProtein,
                goalFat = goalFat,
                goalCarbs = goalCarbs,
                currentMealType = currentMealType,
                onAddMeal = {
                    navController.navigate("select_meal")
                },
                onDeleteLog = onDeleteLog,
                onDebugMealTypeChange = onDebugMealTypeChange
            )
        }

        // Select meal to add
        composable("select_meal") {
            SelectMealScreen(
                meals = allMeals,
                mealNutritions = mealNutritions,
                ambientMealType = currentMealType,
                recentMealIds = recentMealIds,
                favoriteMealIds = favoriteMealIds,
                yesterdayLogs = yesterdayLogs,
                onSelectMeal = { mealId ->
                    val meal = allMeals.find { it.id == mealId }
                    meal?.let {
                        onAddMealToDiary(it.id, it.mealType)
                        navController.popBackStack()
                    }
                },
                onRepeatYesterday = { logs ->
                    logs.forEach { log ->
                        onAddMealToDiary(log.mealId, log.mealType)
                    }
                    navController.popBackStack()
                },
                onToggleFavorite = onToggleFavoriteMeal,
                onCreateMeal = {
                    navController.navigate("create_meal")
                },
                onEditMeal = { mealId ->
                    navController.navigate("edit_meal/$mealId")
                },
                onDeleteMeal = onDeleteMeal,
                onBack = { navController.popBackStack() }
            )
        }

        // Create new meal
        composable("create_meal") {
            CreateMealRoute(
                allProducts = allProducts,
                ambientMealType = currentMealType,
                suggestedProductWeight = suggestedProductWeight,
                onProductWeightUsed = onProductWeightUsed,
                onCreateMeal = onCreateMeal,
                onBack = { navController.popBackStack() }
            )
        }

        // Edit meal
        composable(
            route = "edit_meal/{mealId}",
            arguments = listOf(navArgument("mealId") { type = NavType.LongType })
        ) { backStackEntry ->
            val mealId = backStackEntry.arguments?.getLong("mealId") ?: return@composable
            val meal = allMeals.find { it.id == mealId }
            val mealProducts = mealProductsMap[mealId] ?: emptyList()

            meal?.let {
                EditMealRoute(
                    meal = it,
                    allProducts = allProducts,
                    mealProducts = mealProducts,
                    ambientMealType = currentMealType,
                    suggestedProductWeight = suggestedProductWeight,
                    onProductWeightUsed = onProductWeightUsed,
                    onUpdateMeal = { name, type, products ->
                        onUpdateMeal(it.id, name, type, products)
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // Create product
        composable("create_product") {
            CreateProductScreen(
                onNameChange = { },
                onCaloriesChange = { },
                onProteinChange = { },
                onFatChange = { },
                onCarbsChange = { },
                onCategoryChange = { },
                onSave = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun CreateMealRoute(
    allProducts: List<Product>,
    ambientMealType: String,
    suggestedProductWeight: (Product) -> Int,
    onProductWeightUsed: (Product, Int) -> Unit,
    onCreateMeal: (String, String, List<Pair<Product, Int>>) -> Unit,
    onBack: () -> Unit
) {
    var mealName by remember { mutableStateOf("") }
    var mealType by remember(ambientMealType) { mutableStateOf(ambientMealType) }
    var selectedProducts by remember { mutableStateOf(listOf<Pair<Product, Int>>()) }

    val nutrition = remember(selectedProducts) {
        MealNutrition(
            calories = selectedProducts.sumOf { (p, w) -> (p.caloriesPer100g * w / 100f).toInt() },
            protein = selectedProducts.sumOf { (p, w) -> (p.proteinPer100g * w / 100f).toDouble() }.toFloat(),
            fat = selectedProducts.sumOf { (p, w) -> (p.fatPer100g * w / 100f).toDouble() }.toFloat(),
            carbs = selectedProducts.sumOf { (p, w) -> (p.carbsPer100g * w / 100f).toDouble() }.toFloat()
        )
    }

    CreateEditMealScreen(
        mealName = mealName,
        products = selectedProducts.map { (p, w) -> ProductWithWeightUI(p, w) },
        nutrition = nutrition,
        mealType = mealType,
        ambientMealType = ambientMealType,
        allProducts = allProducts,
        suggestedProductWeight = suggestedProductWeight,
        onNameChange = { mealName = it },
        onMealTypeChange = { mealType = it },
        onRemoveProduct = { index ->
            selectedProducts = selectedProducts.toMutableList().apply { removeAt(index) }
        },
        onAddProduct = { product, weight ->
            onProductWeightUsed(product, weight)
            selectedProducts = selectedProducts + Pair(product, weight)
        },
        onSave = {
            if (mealName.isNotBlank() && selectedProducts.isNotEmpty()) {
                onCreateMeal(mealName, mealType, selectedProducts)
                onBack()
            }
        },
        onBack = onBack
    )
}

@Composable
private fun EditMealRoute(
    meal: Meal,
    allProducts: List<Product>,
    mealProducts: List<MealProduct>,
    ambientMealType: String,
    suggestedProductWeight: (Product) -> Int,
    onProductWeightUsed: (Product, Int) -> Unit,
    onUpdateMeal: (String, String, List<Pair<Product, Int>>) -> Unit,
    onBack: () -> Unit
) {
    var mealName by remember { mutableStateOf(meal.name) }
    var mealType by remember { mutableStateOf(meal.mealType) }

    // Initialize selected products from database
    var selectedProducts by remember {
        mutableStateOf(
            mealProducts.mapNotNull { mp ->
                val product = allProducts.find { it.id == mp.productId }
                product?.let { Pair(it, mp.weightGrams) }
            }
        )
    }

    val nutrition = remember(selectedProducts) {
        MealNutrition(
            calories = selectedProducts.sumOf { (p, w) -> (p.caloriesPer100g * w / 100f).toInt() },
            protein = selectedProducts.sumOf { (p, w) -> (p.proteinPer100g * w / 100f).toDouble() }.toFloat(),
            fat = selectedProducts.sumOf { (p, w) -> (p.fatPer100g * w / 100f).toDouble() }.toFloat(),
            carbs = selectedProducts.sumOf { (p, w) -> (p.carbsPer100g * w / 100f).toDouble() }.toFloat()
        )
    }

    CreateEditMealScreen(
        mealName = mealName,
        products = selectedProducts.map { (p, w) -> ProductWithWeightUI(p, w) },
        nutrition = nutrition,
        mealType = mealType,
        ambientMealType = ambientMealType,
        allProducts = allProducts,
        suggestedProductWeight = suggestedProductWeight,
        onNameChange = { mealName = it },
        onMealTypeChange = { mealType = it },
        onRemoveProduct = { index ->
            selectedProducts = selectedProducts.toMutableList().apply { removeAt(index) }
        },
        onAddProduct = { product, weight ->
            onProductWeightUsed(product, weight)
            selectedProducts = selectedProducts + Pair(product, weight)
        },
        onSave = {
            if (mealName.isNotBlank()) {
                onUpdateMeal(mealName, mealType, selectedProducts)
                onBack()
            }
        },
        onBack = onBack
    )
}
