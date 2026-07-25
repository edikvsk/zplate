package com.zeneyestudio.zplate.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class MealWithProducts(
    @Embedded val meal: Meal,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            MealProduct::class,
            parentColumn = "mealId",
            entityColumn = "productId"
        )
    )
    val products: List<ProductWithWeight>
)

data class ProductWithWeight(
    val product: Product,
    val weightGrams: Int
)
