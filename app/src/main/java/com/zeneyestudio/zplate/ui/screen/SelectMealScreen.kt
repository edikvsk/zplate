package com.zeneyestudio.zplate.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zeneyestudio.zplate.data.model.DailyLog
import com.zeneyestudio.zplate.data.model.Meal
import com.zeneyestudio.zplate.data.model.MealNutrition
import com.zeneyestudio.zplate.ui.components.MealArtwork
import com.zeneyestudio.zplate.ui.components.WellnessBackdrop
import com.zeneyestudio.zplate.ui.components.bouncyClick
import com.zeneyestudio.zplate.ui.components.mealVisual
import java.util.Locale

@Composable
fun SelectMealScreen(
    meals: List<Meal>,
    mealNutritions: Map<Long, MealNutrition>,
    ambientMealType: String,
    recentMealIds: List<Long>,
    favoriteMealIds: Set<Long>,
    yesterdayLogs: List<DailyLog>,
    onSelectMeal: (Long) -> Unit,
    onRepeatYesterday: (List<DailyLog>) -> Unit,
    onToggleFavorite: (Long) -> Unit,
    onCreateMeal: () -> Unit,
    onEditMeal: (Long) -> Unit,
    onDeleteMeal: (Long) -> Unit,
    onBack: () -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    val ambient = mealVisual(ambientMealType)
    val visibleMeals = remember(meals, query, ambientMealType) {
        meals
            .filter { it.name.contains(query.trim(), ignoreCase = true) }
            .sortedWith(
                compareBy<Meal> {
                    if (it.mealType == ambientMealType) 0 else 1
                }.thenBy {
                    when (it.mealType) {
                        "завтрак" -> 0
                        "обед" -> 1
                        "ужин" -> 2
                        else -> 3
                    }
                }.thenBy { it.name.lowercase() }
            )
    }
    val quickMeals = remember(meals, recentMealIds, favoriteMealIds, ambientMealType, query) {
        if (query.isNotBlank()) {
            emptyList()
        } else {
            val favorites = meals
                .filter { it.id in favoriteMealIds && it.mealType == ambientMealType }
                .sortedBy { it.name.lowercase() }
            val recent = recentMealIds
                .mapNotNull { id -> meals.find { it.id == id } }
                .filter { it.mealType == ambientMealType && it.id !in favoriteMealIds }
            (favorites + recent).take(6)
        }
    }
    val catalogMeals = remember(visibleMeals, quickMeals, query) {
        if (query.isBlank()) {
            val quickIds = quickMeals.mapTo(mutableSetOf()) { it.id }
            visibleMeals.filterNot { it.id in quickIds }
        } else {
            visibleMeals
        }
    }
    val yesterdayEntries = remember(yesterdayLogs, meals, ambientMealType) {
        yesterdayLogs.filter { log ->
            log.mealType == ambientMealType && meals.any { it.id == log.mealId }
        }
    }
    val mealGroups = remember(catalogMeals, ambientMealType) {
        val typeOrder = (
            listOf(ambientMealType) + listOf("завтрак", "обед", "ужин")
        ).distinct()
        typeOrder.mapNotNull { type ->
            catalogMeals.filter { it.mealType == type }
                .takeIf { it.isNotEmpty() }
                ?.let { type to it }
        } + catalogMeals
            .filter { it.mealType !in typeOrder }
            .groupBy { it.mealType }
            .map { it.key to it.value }
    }

    WellnessBackdrop(
        accent = ambient.accent,
        topColor = ambient.background,
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Блюда",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = .78f)
                    )
                    Text(
                        text = if (meals.isEmpty()) "Создайте первое" else "${meals.size} в коллекции",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .bouncyClick(onClick = onCreateMeal),
                    shape = CircleShape,
                    color = ambient.accent,
                    shadowElevation = 6.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Rounded.Add,
                            contentDescription = "Новое блюдо",
                            tint = if (androidx.compose.foundation.isSystemInDarkTheme()) {
                                MaterialTheme.colorScheme.background
                            } else {
                                Color.White
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Найти блюдо") },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = .94f),
                    focusedBorderColor = ambient.accent.copy(alpha = .58f),
                    unfocusedBorderColor = Color.Transparent
                )
            )
            Spacer(Modifier.height(14.dp))

            if (visibleMeals.isEmpty()) {
                EmptyMeals(
                    accent = ambient.accent,
                    mealType = ambientMealType,
                    onCreateMeal = onCreateMeal
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (query.isBlank() && yesterdayEntries.isNotEmpty()) {
                        item(key = "repeat-yesterday") {
                            RepeatYesterdayCard(
                                mealType = ambientMealType,
                                meals = yesterdayEntries.mapNotNull { log ->
                                    meals.find { it.id == log.mealId }
                                },
                                accent = ambient.accent,
                                onClick = { onRepeatYesterday(yesterdayEntries) }
                            )
                        }
                    }

                    if (quickMeals.isNotEmpty()) {
                        item(key = "quick-meals-title") {
                            Text(
                                text = "Быстро",
                                modifier = Modifier.padding(top = 6.dp, start = 2.dp),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        item(key = "quick-meals") {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(quickMeals, key = { "quick-${it.id}" }) { meal ->
                                    QuickMealCard(
                                        meal = meal,
                                        nutrition = mealNutritions[meal.id],
                                        isFavorite = meal.id in favoriteMealIds,
                                        onClick = { onSelectMeal(meal.id) }
                                    )
                                }
                            }
                        }
                    }

                    mealGroups.forEach { (mealType, sectionMeals) ->
                        item(key = "section-$mealType") {
                            MealSectionHeader(
                                mealType = mealType,
                                count = sectionMeals.size
                            )
                        }
                        items(sectionMeals, key = { it.id }) { meal ->
                            MealCollectionCard(
                                meal = meal,
                                nutrition = mealNutritions[meal.id],
                                isFavorite = meal.id in favoriteMealIds,
                                onSelect = { onSelectMeal(meal.id) },
                                onToggleFavorite = { onToggleFavorite(meal.id) },
                                onEdit = { onEditMeal(meal.id) },
                                onDelete = { onDeleteMeal(meal.id) }
                            )
                        }
                    }
                    item { Spacer(Modifier.height(10.dp)) }
                }
            }
        }
    }
}

@Composable
private fun RepeatYesterdayCard(
    mealType: String,
    meals: List<Meal>,
    accent: Color,
    onClick: () -> Unit
) {
    val names = meals.map { it.name }.distinct().joinToString(" · ")
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .bouncyClick(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = accent.copy(alpha = .13f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(15.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = .9f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    MealArtwork(mealType = mealType, size = 44.dp)
                }
            }
            Spacer(Modifier.size(11.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Повторить вчера",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = names,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.Rounded.History,
                contentDescription = "Повторить вчерашний приём пищи",
                tint = accent
            )
        }
    }
}

@Composable
private fun QuickMealCard(
    meal: Meal,
    nutrition: MealNutrition?,
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    val visual = mealVisual(meal.mealType)
    Surface(
        modifier = Modifier
            .width(180.dp)
            .bouncyClick(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = .95f)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(46.dp),
                shape = RoundedCornerShape(14.dp),
                color = visual.background
            ) {
                Box(contentAlignment = Alignment.Center) {
                    MealArtwork(meal.mealType, 42.dp)
                }
            }
            Spacer(Modifier.size(9.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = meal.name,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (isFavorite) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = "Привычное",
                            modifier = Modifier.size(14.dp),
                            tint = visual.accent
                        )
                    }
                }
                nutrition?.let {
                    Text(
                        text = "${it.calories} ккал",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun MealSectionHeader(
    mealType: String,
    count: Int
) {
    val visual = mealVisual(mealType)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = mealType.replaceFirstChar { it.titlecase(Locale.getDefault()) },
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = visual.accent
        )
        Spacer(Modifier.size(7.dp))
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.size(10.dp))
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(1.dp),
            color = visual.accent.copy(alpha = .25f)
        ) {}
    }
}

@Composable
private fun MealCollectionCard(
    meal: Meal,
    nutrition: MealNutrition?,
    isFavorite: Boolean,
    onSelect: () -> Unit,
    onToggleFavorite: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
    val visual = mealVisual(meal.mealType)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .bouncyClick(onClick = onSelect),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = .97f)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(76.dp),
                shape = RoundedCornerShape(19.dp),
                color = visual.background
            ) {
                Box(contentAlignment = Alignment.Center) {
                    MealArtwork(mealType = meal.mealType, size = 70.dp)
                }
            }
            Spacer(Modifier.size(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = meal.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = .8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = buildString {
                        append(meal.mealType.replaceFirstChar { it.titlecase(Locale.getDefault()) })
                        nutrition?.let { append("  ·  ${it.calories} ккал") }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = visual.accent
                )
            }
            Box {
                IconButton(onClick = { menuOpen = true }) {
                    Icon(
                        Icons.Rounded.MoreVert,
                        contentDescription = "Действия",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                DropdownMenu(
                    expanded = menuOpen,
                    onDismissRequest = { menuOpen = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                if (isFavorite) {
                                    "Убрать из привычных"
                                } else {
                                    "В привычные"
                                }
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isFavorite) {
                                    Icons.Rounded.Star
                                } else {
                                    Icons.Rounded.StarOutline
                                },
                                contentDescription = null
                            )
                        },
                        onClick = {
                            menuOpen = false
                            onToggleFavorite()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Изменить") },
                        leadingIcon = { Icon(Icons.Rounded.Edit, contentDescription = null) },
                        onClick = {
                            menuOpen = false
                            onEdit()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Удалить", color = MaterialTheme.colorScheme.error) },
                        leadingIcon = {
                            Icon(
                                Icons.Rounded.DeleteOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = {
                            menuOpen = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyMeals(
    accent: Color,
    mealType: String,
    onCreateMeal: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MealArtwork(mealType, 154.dp)
        Text(
            text = "Здесь появятся ваши блюда",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Сохраните любимое один раз",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(18.dp))
        Surface(
            modifier = Modifier
                .height(48.dp)
                .bouncyClick(onClick = onCreateMeal),
            shape = RoundedCornerShape(18.dp),
            color = accent
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = null,
                    tint = if (androidx.compose.foundation.isSystemInDarkTheme()) {
                        MaterialTheme.colorScheme.background
                    } else {
                        Color.White
                    }
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    "Создать",
                    color = if (androidx.compose.foundation.isSystemInDarkTheme()) {
                        MaterialTheme.colorScheme.background
                    } else {
                        Color.White
                    }
                )
            }
        }
    }
}
