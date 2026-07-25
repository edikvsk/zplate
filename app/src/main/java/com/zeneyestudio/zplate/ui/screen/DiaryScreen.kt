package com.zeneyestudio.zplate.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zeneyestudio.zplate.data.model.DailyLog
import com.zeneyestudio.zplate.data.model.Meal
import com.zeneyestudio.zplate.data.model.MealNutrition
import com.zeneyestudio.zplate.ui.components.EmptyMealCard
import com.zeneyestudio.zplate.ui.components.MealCard
import com.zeneyestudio.zplate.ui.components.ProgressCard
import com.zeneyestudio.zplate.util.TimeHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(
    date: String,
    dailyCalories: Int,
    dailyProtein: Float,
    dailyFat: Float,
    dailyCarbs: Float,
    goalCalories: Int,
    logs: List<DailyLog>,
    meals: Map<Long, Meal>,
    mealNutritions: Map<Long, MealNutrition>,
    onAddMeal: (String) -> Unit,
    onEditMeal: (Long) -> Unit,
    onDeleteLog: (DailyLog) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Дневник", fontWeight = FontWeight.Bold)
                        Text(
                            text = date,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // Progress card
            ProgressCard(
                currentCalories = dailyCalories,
                goalCalories = goalCalories,
                protein = dailyProtein,
                fat = dailyFat,
                carbs = dailyCarbs
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Meal sections
            val mealTypes = listOf(
                Triple("завтрак", "", "Завтрак"),
                Triple("обед", "", "Обед"),
                Triple("ужин", "", "Ужин"),
                Triple("перекус", "", "Перекус")
            )

            mealTypes.forEach { (type, emoji, title) ->
                val typeLogs = logs.filter { it.mealType == type }

                // Section header with add button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when(type) {
                                "завтрак" -> MaterialTheme.colorScheme.primaryContainer
                                "обед" -> MaterialTheme.colorScheme.secondaryContainer
                                "ужин" -> MaterialTheme.colorScheme.tertiaryContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                com.zeneyestudio.zplate.ui.components.MealArtwork(
                                    mealType = type,
                                    size = 32.dp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    TextButton(onClick = { onAddMeal(type) }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Добавить")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Meals in this section
                if (typeLogs.isNotEmpty()) {
                    typeLogs.forEach { log ->
                        val meal = meals[log.mealId]
                        if (meal != null) {
                            val nutrition = mealNutritions[meal.id] ?: MealNutrition(0, 0f, 0f, 0f)
                            MealCard(
                                mealName = meal.name,
                                nutrition = nutrition,
                                emoji = emoji,
                                onDelete = { onDeleteLog(log) },
                                onEdit = { onEditMeal(meal.id) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                } else {
                    EmptyMealCard(mealType = type)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
