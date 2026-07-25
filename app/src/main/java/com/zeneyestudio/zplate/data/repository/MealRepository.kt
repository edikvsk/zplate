package com.zeneyestudio.zplate.data.repository

import com.zeneyestudio.zplate.data.db.MealDao
import com.zeneyestudio.zplate.data.model.Meal
import com.zeneyestudio.zplate.data.model.MealNutrition
import com.zeneyestudio.zplate.data.model.MealProduct
import com.zeneyestudio.zplate.data.model.Product
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MealRepository @Inject constructor(
    private val mealDao: MealDao
) {
    fun getAllMeals(): Flow<List<Meal>> = mealDao.getAllMeals()

    fun getMealsByType(mealType: String): Flow<List<Meal>> = mealDao.getMealsByType(mealType)

    suspend fun getMealById(id: Long): Meal? = mealDao.getMealById(id)

    suspend fun insertMeal(meal: Meal): Long = mealDao.insertMeal(meal)

    suspend fun updateMeal(meal: Meal) = mealDao.updateMeal(meal)

    suspend fun deleteMeal(meal: Meal) = mealDao.deleteMeal(meal)

    suspend fun deleteMealById(mealId: Long) = mealDao.deleteMealById(mealId)

    fun getMealProducts(mealId: Long): Flow<List<MealProduct>> = mealDao.getMealProducts(mealId)

    suspend fun insertMealProduct(mealProduct: MealProduct) = mealDao.insertMealProduct(mealProduct)

    suspend fun insertMealProducts(mealProducts: List<MealProduct>) = mealDao.insertMealProducts(mealProducts)

    suspend fun deleteMealProduct(mealProduct: MealProduct) = mealDao.deleteMealProduct(mealProduct)

    suspend fun deleteAllMealProducts(mealId: Long) = mealDao.deleteAllMealProducts(mealId)

    fun calculateMealNutrition(products: List<Pair<Product, Int>>): MealNutrition {
        var totalCalories = 0
        var totalProtein = 0f
        var totalFat = 0f
        var totalCarbs = 0f

        for ((product, weight) in products) {
            val multiplier = weight / 100f
            totalCalories += (product.caloriesPer100g * multiplier).toInt()
            totalProtein += product.proteinPer100g * multiplier
            totalFat += product.fatPer100g * multiplier
            totalCarbs += product.carbsPer100g * multiplier
        }

        return MealNutrition(
            calories = totalCalories,
            protein = totalProtein,
            fat = totalFat,
            carbs = totalCarbs
        )
    }
}
