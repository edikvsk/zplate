package com.zeneyestudio.zplate.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.zeneyestudio.zplate.data.model.DailyLog
import com.zeneyestudio.zplate.data.model.Meal
import com.zeneyestudio.zplate.data.model.MealProduct
import com.zeneyestudio.zplate.data.model.Product

@Database(
    entities = [
        Product::class,
        Meal::class,
        MealProduct::class,
        DailyLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun mealDao(): MealDao
    abstract fun dailyLogDao(): DailyLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mydnevnik_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
