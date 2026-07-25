package com.zeneyestudio.zplate.data.db

import androidx.room.*
import com.zeneyestudio.zplate.data.model.Meal
import com.zeneyestudio.zplate.data.model.MealProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Query("SELECT * FROM meals ORDER BY name ASC")
    fun getAllMeals(): Flow<List<Meal>>

    @Query("SELECT * FROM meals WHERE mealType = :mealType ORDER BY name ASC")
    fun getMealsByType(mealType: String): Flow<List<Meal>>

    @Query("SELECT * FROM meals WHERE id = :id")
    suspend fun getMealById(id: Long): Meal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: Meal): Long

    @Update
    suspend fun updateMeal(meal: Meal)

    @Delete
    suspend fun deleteMeal(meal: Meal)

    @Query("DELETE FROM meals WHERE id = :mealId")
    suspend fun deleteMealById(mealId: Long)

    @Query("SELECT * FROM meal_products WHERE mealId = :mealId")
    fun getMealProducts(mealId: Long): Flow<List<MealProduct>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealProduct(mealProduct: MealProduct)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealProducts(mealProducts: List<MealProduct>)

    @Delete
    suspend fun deleteMealProduct(mealProduct: MealProduct)

    @Query("DELETE FROM meal_products WHERE mealId = :mealId")
    suspend fun deleteAllMealProducts(mealId: Long)
}
