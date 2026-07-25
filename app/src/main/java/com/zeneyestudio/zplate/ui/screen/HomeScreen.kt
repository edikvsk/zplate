package com.zeneyestudio.zplate.ui.screen

import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import com.zeneyestudio.zplate.ui.theme.BreakfastColor
import com.zeneyestudio.zplate.ui.theme.DinnerColor
import com.zeneyestudio.zplate.ui.theme.LunchColor
import com.zeneyestudio.zplate.ui.components.bouncyClick
import com.zeneyestudio.zplate.ui.components.MealArtwork
import com.zeneyestudio.zplate.data.model.DailyLog
import com.zeneyestudio.zplate.data.model.Meal
import com.zeneyestudio.zplate.data.model.MealNutrition
import kotlin.math.roundToInt
import kotlin.math.absoluteValue

private data class DayMood(
    val label: String,
    val accent: Color,
    val backgroundTop: Color,
    val backgroundBottom: Color,
    val glow: Color
)

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun HomeScreen(
    currentCalories: Int,
    dailyCaloriesByDate: Map<String, Int>,
    goalCalories: Int,
    currentProtein: Float,
    currentFat: Float,
    currentCarbs: Float,
    todayLogs: List<DailyLog>,
    historyLogs: List<DailyLog>,
    meals: List<Meal>,
    mealNutritions: Map<Long, MealNutrition>,
    goalProtein: Int,
    goalFat: Int,
    goalCarbs: Int,
    currentMealType: String,
    onAddMeal: () -> Unit,
    onDeleteLog: (DailyLog) -> Unit,
    onDebugMealTypeChange: (String) -> Unit = {},
    onDayClick: (String) -> Unit = {}
) {
    val dark = isSystemInDarkTheme()
    var macroExpanded by remember { mutableStateOf(false) }
    val mood = moodFor(currentMealType)
    val progress = if (goalCalories > 0) currentCalories.toFloat() / goalCalories else 0f
    val caloriesByMealType = remember(todayLogs, mealNutritions) {
        todayLogs
            .groupBy { it.mealType }
            .mapValues { (_, logs) ->
                logs.sumOf { log -> mealNutritions[log.mealId]?.calories ?: 0 }
            }
    }
    val breakfastProgress = if (goalCalories > 0) {
        (caloriesByMealType["завтрак"].orZero() / (goalCalories * .3f)).coerceIn(0f, 1f)
    } else {
        0f
    }
    val lunchProgress = if (goalCalories > 0) {
        (caloriesByMealType["обед"].orZero() / (goalCalories * .4f)).coerceIn(0f, 1f)
    } else {
        0f
    }
    val dinnerProgress = if (goalCalories > 0) {
        (caloriesByMealType["ужин"].orZero() / (goalCalories * .3f)).coerceIn(0f, 1f)
    } else {
        0f
    }
    val dayCircleClosed = listOf("завтрак", "обед", "ужин").all { type ->
        todayLogs.any { it.mealType == type }
    }
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(1100, easing = FastOutSlowInEasing),
        label = "calorie progress"
    )
    val accent by animateColorAsState(
        targetValue = mood.accent,
        animationSpec = tween(650),
        label = "meal accent"
    )
    val clusterTransition = rememberInfiniteTransition(label = "calorie cluster pulse")
    val clusterPulse by clusterTransition.animateFloat(
        initialValue = .98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "calorie cluster scale"
    )
    val pagerState = rememberPagerState(pageCount = { 3 })
    LaunchedEffect(Unit) {
        pagerState.scrollToPage(1)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(mood.backgroundTop, mood.backgroundBottom)
                )
            )
    ) {
        AtmosphericBackground(mood)

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val pageOffset = (
                (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            ).absoluteValue.coerceIn(0f, 1f)
            val pageScale = .8f + (1f - pageOffset) * .2f

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = pageScale
                        scaleY = pageScale
                        alpha = .32f + (1f - pageOffset) * .68f
                        translationY = pageOffset * 22.dp.toPx()
                        rotationY = (pagerState.currentPage - page) *
                            pagerState.currentPageOffsetFraction * 9f
                        cameraDistance = 14f * density
                    }
            ) {
                if (page == 0) {
                    DailyStatisticsPage(
                        dailyCaloriesByDate = dailyCaloriesByDate,
                        historyLogs = historyLogs,
                        goalCalories = goalCalories,
                        accent = accent,
                        onDayClick = onDayClick
                    )
                } else if (page == 2) {
                    DailyRationPage(
                        todayLogs = todayLogs,
                        meals = meals,
                        mealNutritions = mealNutritions,
                        currentCalories = currentCalories,
                        goalCalories = goalCalories,
                        currentProtein = currentProtein.toInt(),
                        currentFat = currentFat.toInt(),
                        currentCarbs = currentCarbs.toInt(),
                        goalProtein = goalProtein,
                        goalFat = goalFat,
                        goalCarbs = goalCarbs,
                        currentMealType = currentMealType,
                        accent = accent,
                        onAddMeal = onAddMeal,
                        onDeleteLog = onDeleteLog
                    )
                } else {
                    Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
            Text(
                text = mood.label,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            DebugMealSwitcher(
                selectedType = currentMealType,
                accent = accent,
                onSelected = onDebugMealTypeChange
            )

            Spacer(Modifier.weight(1f))
            Column(
                modifier = Modifier.offset(y = (-18).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(292.dp)
                        .scale(clusterPulse),
                    contentAlignment = Alignment.Center
                ) {
                    CalorieRing(
                        progress = animatedProgress,
                        breakfastProgress = breakfastProgress,
                        lunchProgress = lunchProgress,
                        dinnerProgress = dinnerProgress,
                        dayClosed = dayCircleClosed,
                        currentCalories = currentCalories,
                        goalCalories = goalCalories,
                        accent = accent,
                        onAddMeal = onAddMeal
                    )
                    MacroHandle(
                        expanded = macroExpanded,
                        accent = accent,
                        onToggle = { macroExpanded = !macroExpanded },
                        onExpand = { macroExpanded = true },
                        onCollapse = { macroExpanded = false },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = 19.dp)
                    )
                }
                Spacer(Modifier.height(25.dp))
                AnimatedVisibility(
                    visible = macroExpanded,
                    enter = expandVertically(expandFrom = Alignment.Top) +
                        slideInVertically { -it / 3 } + fadeIn(),
                    exit = shrinkVertically(shrinkTowards = Alignment.Top) +
                        slideOutVertically { -it / 3 } + fadeOut()
                ) {
                    MacroPanel(
                        currentCalories = currentCalories,
                        goalCalories = goalCalories,
                        currentProtein = currentProtein.toInt(),
                        goalProtein = goalProtein,
                        currentFat = currentFat.toInt(),
                        goalFat = goalFat,
                        currentCarbs = currentCarbs.toInt(),
                        goalCarbs = goalCarbs,
                        accent = accent,
                        dark = dark
                    )
                }
            }
                    Spacer(Modifier.weight(1f))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            Modifier
                                .size(6.dp)
                                .background(
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = .18f),
                                    CircleShape
                                )
                        )
                        Box(
                            Modifier
                                .size(18.dp, 6.dp)
                                .background(accent, CircleShape)
                        )
                        Box(
                            Modifier
                                .size(6.dp)
                                .background(
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = .18f),
                                    CircleShape
                                )
                        )
                    }
                    }
                }
            }
        }
    }
}

@Composable
private fun DebugMealSwitcher(
    selectedType: String,
    accent: Color,
    onSelected: (String) -> Unit
) {
    val onAccent = if (isSystemInDarkTheme()) Color(0xFF101713) else Color.White
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = .76f)
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            listOf(
                "завтрак" to "Завтрак",
                "обед" to "Обед",
                "ужин" to "Ужин"
            ).forEach { (type, title) ->
                val selected = selectedType == type
                Surface(
                    modifier = Modifier.bouncyClick { onSelected(type) },
                    shape = RoundedCornerShape(15.dp),
                    color = if (selected) accent else Color.Transparent
                ) {
                    Text(
                        text = title,
                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (selected) onAccent else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyStatisticsPage(
    dailyCaloriesByDate: Map<String, Int>,
    historyLogs: List<DailyLog>,
    goalCalories: Int,
    accent: Color,
    onDayClick: (String) -> Unit
) {
    var selectedPeriod by remember { mutableIntStateOf(7) }
    val dates = remember(selectedPeriod) {
        (selectedPeriod - 1 downTo 0).map { daysAgo ->
            LocalDate.now().minusDays(daysAgo.toLong())
        }
    }
    val values = dates.map { date ->
        dailyCaloriesByDate[date.format(DateTimeFormatter.ISO_LOCAL_DATE)] ?: 0
    }
    val columns = 7
    val rows = dates.chunked(columns)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = .82f)
        ) {
            Row(
                modifier = Modifier.padding(5.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(7, 14, 30).forEach { period ->
                    val selected = selectedPeriod == period
                    Surface(
                        modifier = Modifier.bouncyClick { selectedPeriod = period },
                        shape = RoundedCornerShape(15.dp),
                        color = if (selected) accent else Color.Transparent
                    ) {
                        Text(
                            text = "$period дней",
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selected) {
                                if (isSystemInDarkTheme()) Color(0xFF101713) else Color.White
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = .94f)
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").forEach { dayName ->
                        Text(
                            text = dayName,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                rows.forEach { week ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        week.forEach { date ->
                            val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                            val calories = dailyCaloriesByDate[dateStr] ?: 0
                            val isToday = date == LocalDate.now()
                            val hasData = calories > 0
                            val inGoal = hasData && calories in (goalCalories * .85f).roundToInt()..(goalCalories * 1.15f).roundToInt()
                            val overGoal = hasData && calories > (goalCalories * 1.15f).roundToInt()

                            val cellBg = when {
                                isToday -> accent.copy(alpha = .18f)
                                inGoal -> Color(0xFF4CAF50).copy(alpha = .12f)
                                overGoal -> Color(0xFFF44336).copy(alpha = .08f)
                                hasData -> accent.copy(alpha = .06f)
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .35f)
                            }
                            val cellBorder = when {
                                isToday -> accent
                                inGoal -> Color(0xFF4CAF50).copy(alpha = .5f)
                                overGoal -> Color(0xFFF44336).copy(alpha = .3f)
                                else -> Color.Transparent
                            }

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .bouncyClick { onDayClick(dateStr) },
                                shape = RoundedCornerShape(10.dp),
                                color = cellBg,
                                border = if (isToday || inGoal || overGoal) {
                                    BorderStroke(1.5.dp, cellBorder)
                                } else null
                            ) {
                                Column(
                                    modifier = Modifier.padding(2.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "${date.dayOfMonth}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                                        color = when {
                                            isToday -> accent
                                            inGoal -> Color(0xFF4CAF50)
                                            overGoal -> Color(0xFFF44336)
                                            hasData -> MaterialTheme.colorScheme.onSurface
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .5f)
                                        }
                                    )
                                    if (hasData) {
                                        Text(
                                            text = "$calories",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                        repeat(columns - week.size) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                    Spacer(Modifier.height(5.dp))
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                Modifier
                    .size(18.dp, 6.dp)
                    .background(accent, CircleShape)
            )
            Box(
                Modifier
                    .size(6.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = .18f), CircleShape)
            )
            Box(
                Modifier
                    .size(6.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = .18f), CircleShape)
            )
        }
    }
}

@Composable
private fun StatisticSummary(value: String, label: String, accent: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = accent
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatisticDayBar(
    day: String,
    value: Int,
    maximum: Int,
    accent: Color,
    isToday: Boolean,
    width: Dp,
    showValue: Boolean,
    dateStr: String = "",
    onDayClick: (String) -> Unit = {}
) {
    val targetHeight = (112f * value / maximum).coerceAtLeast(if (value > 0) 7f else 2f).dp
    val animatedHeight by animateDpAsState(
        targetValue = targetHeight,
        animationSpec = tween(650, easing = FastOutSlowInEasing),
        label = "daily calorie bar"
    )

    Column(
        modifier = Modifier.bouncyClick { if (dateStr.isNotEmpty()) onDayClick(dateStr) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (showValue && value > 0) value.toString() else "",
            style = MaterialTheme.typography.labelSmall,
            color = if (isToday) accent else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(if (showValue) 7.dp else 2.dp))
        Box(
            modifier = Modifier
                .width(width)
                .height(118.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .72f),
                    RoundedCornerShape(width / 2)
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(animatedHeight)
                    .background(
                        if (isToday) accent else accent.copy(alpha = .48f),
                        RoundedCornerShape(width / 2)
                    )
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = day,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
            color = if (isToday) accent else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DailyRationPage(
    todayLogs: List<DailyLog>,
    meals: List<Meal>,
    mealNutritions: Map<Long, MealNutrition>,
    currentCalories: Int,
    goalCalories: Int,
    currentProtein: Int,
    currentFat: Int,
    currentCarbs: Int,
    goalProtein: Int,
    goalFat: Int,
    goalCarbs: Int,
    currentMealType: String,
    accent: Color,
    onAddMeal: () -> Unit,
    onDeleteLog: (DailyLog) -> Unit
) {
    var logToDelete by remember { mutableStateOf<DailyLog?>(null) }
    val visibleEntries = todayLogs
        .sortedWith(
            compareBy<DailyLog> {
                when (it.mealType) {
                    "завтрак" -> 0
                    "обед" -> 1
                    "ужин" -> 2
                    else -> 3
                }
            }.thenBy { it.timestamp }
        )
        .mapNotNull { log ->
            meals.find { it.id == log.mealId }?.let { meal ->
                Triple(log, meal, mealNutritions[meal.id])
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Сегодня",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = .92f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 13.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MacroValue("К", currentCalories, goalCalories, accent)
                MacroValue("Б", currentProtein, goalProtein, Color(0xFF73B991))
                MacroValue("Ж", currentFat, goalFat, Color(0xFFE58D69))
                MacroValue("У", currentCarbs, goalCarbs, Color(0xFF929CE0))
            }
        }

        Spacer(Modifier.height(14.dp))
        if (visibleEntries.isEmpty()) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                MealArtwork(currentMealType, 138.dp)
                Text(
                    text = "Рацион пока пуст",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                visibleEntries.forEach { (log, meal, nutrition) ->
                    RationMealCard(
                        meal = meal,
                        nutrition = nutrition,
                        onDelete = { logToDelete = log }
                    )
                }
                Spacer(Modifier.height(4.dp))
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .bouncyClick(onClick = onAddMeal),
            shape = RoundedCornerShape(18.dp),
            color = accent
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = null,
                    tint = if (isSystemInDarkTheme()) Color(0xFF101713) else Color.White
                )
                Spacer(Modifier.size(7.dp))
                Text(
                    text = "Добавить блюдо",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSystemInDarkTheme()) Color(0xFF101713) else Color.White
                )
            }
        }
        Spacer(Modifier.height(13.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(2) {
                Box(
                    Modifier
                        .size(6.dp)
                        .background(
                            MaterialTheme.colorScheme.onSurface.copy(alpha = .18f),
                            CircleShape
                        )
                )
            }
            Box(
                Modifier
                    .size(18.dp, 6.dp)
                    .background(accent, CircleShape)
            )
        }
    }

    logToDelete?.let { log ->
        val mealName = meals.find { it.id == log.mealId }?.name.orEmpty()
        AlertDialog(
            onDismissRequest = { logToDelete = null },
            title = { Text("Убрать из рациона?") },
            text = {
                Text(
                    if (mealName.isBlank()) {
                        "Блюдо будет удалено только из сегодняшнего рациона."
                    } else {
                        "«$mealName» будет удалено только из сегодняшнего рациона."
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteLog(log)
                        logToDelete = null
                    }
                ) {
                    Text("Убрать", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { logToDelete = null }) {
                    Text("Отмена")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun RationMealCard(
    meal: Meal,
    nutrition: MealNutrition?,
    showDelete: Boolean = true,
    onDelete: () -> Unit
) {
    val visual = com.zeneyestudio.zplate.ui.components.mealVisual(meal.mealType)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = .94f)
    ) {
        Row(
            modifier = Modifier.padding(9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(17.dp),
                color = visual.background
            ) {
                Box(contentAlignment = Alignment.Center) {
                    MealArtwork(meal.mealType, 59.dp)
                }
            }
            Spacer(Modifier.size(13.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = meal.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = meal.mealType.replaceFirstChar {
                        it.titlecase(Locale.getDefault())
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = visual.accent
                )
            }
            nutrition?.let {
                Text(
                    text = "${it.calories} ккал",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (showDelete) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = "Убрать из рациона",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = .78f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AtmosphericBackground(mood: DayMood) {
    val transition = rememberInfiniteTransition(label = "background motion")
    val drift by transition.animateFloat(
        initialValue = -18f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(6500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "light drift"
    )
    val shimmer by transition.animateFloat(
        initialValue = 0.16f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "light shimmer"
    )

    val surfaceColor = MaterialTheme.colorScheme.surface
    val onBgColor = MaterialTheme.colorScheme.onBackground

    Canvas(Modifier.fillMaxSize()) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(mood.glow.copy(alpha = shimmer), Color.Transparent),
                center = Offset(size.width * .18f + drift, size.height * .18f),
                radius = size.width * .72f
            ),
            radius = size.width * .72f,
            center = Offset(size.width * .18f + drift, size.height * .18f)
        )
        drawCircle(
            color = surfaceColor.copy(alpha = .15f),
            radius = size.width * .42f,
            center = Offset(size.width * .92f - drift, size.height * .72f)
        )
        if (mood.label == "Ужин") {
            listOf(.18f to .28f, .78f to .18f, .86f to .42f, .25f to .7f, .65f to .8f)
                .forEachIndexed { index, (x, y) ->
                    drawCircle(
                        color = onBgColor.copy(alpha = .28f + index * .05f),
                        radius = if (index % 2 == 0) 3.5f else 2f,
                        center = Offset(size.width * x, size.height * y + drift)
                    )
                }
        } else {
            drawOval(
                color = mood.accent.copy(alpha = .07f),
                topLeft = Offset(size.width * .58f, size.height * .56f + drift),
                size = Size(size.width * .62f, size.height * .2f)
            )
        }
    }
}

@Composable
private fun CalorieRing(
    progress: Float,
    breakfastProgress: Float,
    lunchProgress: Float,
    dinnerProgress: Float,
    dayClosed: Boolean,
    currentCalories: Int,
    goalCalories: Int,
    accent: Color,
    onAddMeal: () -> Unit
) {
    val view = LocalView.current
    val onAccent = if (isSystemInDarkTheme()) Color(0xFF101713) else Color.White
    val transition = rememberInfiniteTransition(label = "ring animation")
    val sweepGlow by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(7000)),
        label = "ring glow"
    )
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(
        targetValue = if (pressed) .9f else 1f,
        animationSpec = tween(140),
        label = "add button press"
    )
    val buttonRotation by animateFloatAsState(
        targetValue = if (pressed) 45f else 0f,
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "add icon rotation"
    )
    val animatedBreakfast by animateFloatAsState(
        targetValue = breakfastProgress,
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label = "breakfast ring progress"
    )
    val animatedLunch by animateFloatAsState(
        targetValue = lunchProgress,
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label = "lunch ring progress"
    )
    val animatedDinner by animateFloatAsState(
        targetValue = dinnerProgress,
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label = "dinner ring progress"
    )
    val segmentGap by animateFloatAsState(
        targetValue = if (dayClosed) 2f else 9f,
        animationSpec = tween(850, easing = FastOutSlowInEasing),
        label = "ring segment gap"
    )
    val closureGlow by animateFloatAsState(
        targetValue = if (dayClosed) 1f else 0f,
        animationSpec = tween(1100),
        label = "day closure glow"
    )

    Box(
        modifier = Modifier.size(292.dp),
        contentAlignment = Alignment.Center
    ) {
        val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
        val onSurfaceColor = MaterialTheme.colorScheme.onSurface
        val segmentColors = listOf(BreakfastColor, LunchColor, DinnerColor)
        val segmentProgress = listOf(animatedBreakfast, animatedLunch, animatedDinner)

        Canvas(Modifier.fillMaxSize()) {
            val stroke = 14.dp.toPx()
            val inset = stroke / 2
            val arcSize = Size(size.width - stroke, size.height - stroke)
            val segmentSweep = 120f - segmentGap
            segmentColors.forEachIndexed { index, color ->
                val startAngle = -90f + index * 120f + segmentGap / 2f
                drawArc(
                    color = surfaceVariantColor.copy(alpha = .58f),
                    startAngle = startAngle,
                    sweepAngle = segmentSweep,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(stroke, cap = StrokeCap.Round)
                )
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = segmentSweep * segmentProgress[index],
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(stroke, cap = StrokeCap.Round)
                )
            }
            if (closureGlow > 0f) {
                drawArc(
                    color = onSurfaceColor.copy(alpha = .12f * closureGlow),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = Offset(inset - 5.dp.toPx(), inset - 5.dp.toPx()),
                    size = Size(
                        arcSize.width + 10.dp.toPx(),
                        arcSize.height + 10.dp.toPx()
                    ),
                    style = Stroke(2.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            drawArc(
                color = onSurfaceColor.copy(alpha = .22f),
                startAngle = sweepGlow,
                sweepAngle = 22f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(3.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Surface(
            modifier = Modifier.size(244.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface.copy(alpha = .97f),
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$currentCalories",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "из $goalCalories ккал · ${(progress * 100).roundToInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(18.dp))
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .scale(buttonScale)
                        .clickable(
                            interactionSource = interaction,
                            indication = null,
                            onClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                                onAddMeal()
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(accent.copy(alpha = .14f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .shadow(9.dp, CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(lerp(accent, Color.White, .2f), accent)
                                ),
                                CircleShape
                            )
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = .38f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Добавить приём пищи",
                            modifier = Modifier
                                .size(29.dp)
                                .graphicsLayer { rotationZ = buttonRotation },
                            tint = onAccent
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Добавить",
                    style = MaterialTheme.typography.labelSmall,
                    color = accent
                )
            }
        }
    }
}

private fun Int?.orZero(): Float = (this ?: 0).toFloat()

@Composable
fun DayDetailScreen(
    date: String,
    dayLogs: List<DailyLog>,
    meals: List<Meal>,
    mealNutritions: Map<Long, MealNutrition>,
    currentCalories: Int,
    goalCalories: Int,
    currentProtein: Int,
    currentFat: Int,
    currentCarbs: Int,
    goalProtein: Int,
    goalFat: Int,
    goalCarbs: Int,
    currentMealType: String,
    accent: Color,
    onBack: () -> Unit
) {
    val mood = moodFor(currentMealType)
    val parsedDate = remember(date) {
        LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)
    }
    val displayDate = remember(parsedDate) {
        parsedDate.format(DateTimeFormatter.ofPattern("d MMMM", Locale("ru")))
    }
    val visibleEntries = dayLogs
        .sortedWith(
            compareBy<DailyLog> {
                when (it.mealType) {
                    "завтрак" -> 0
                    "обед" -> 1
                    "ужин" -> 2
                    else -> 3
                }
            }.thenBy { it.timestamp }
        )
        .mapNotNull { log ->
            meals.find { it.id == log.mealId }?.let { meal ->
                Triple(log, meal, mealNutritions[meal.id])
            }
        }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(mood.backgroundTop, mood.backgroundBottom)
                )
            )
    ) {
        AtmosphericBackground(mood)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(44.dp)
                    .bouncyClick { onBack() },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface.copy(alpha = .96f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Rounded.ArrowBack,
                        contentDescription = "Назад",
                        tint = mood.accent
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            Text(
                text = displayDate,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.size(44.dp))
        }

        Spacer(Modifier.height(16.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = .92f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 13.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MacroValue("К", currentCalories, goalCalories, mood.accent)
                MacroValue("Б", currentProtein, goalProtein, Color(0xFF73B991))
                MacroValue("Ж", currentFat, goalFat, Color(0xFFE58D69))
                MacroValue("У", currentCarbs, goalCarbs, Color(0xFF929CE0))
            }
        }

        Spacer(Modifier.height(14.dp))
        if (visibleEntries.isEmpty()) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                MealArtwork("обед", 138.dp)
                Text(
                    text = "Рацион пуст",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                visibleEntries.forEach { (log, meal, nutrition) ->
                    RationMealCard(
                        meal = meal,
                        nutrition = nutrition,
                        showDelete = false,
                        onDelete = {}
                    )
                }
                Spacer(Modifier.height(4.dp))
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(3) {
                Box(
                    Modifier
                        .size(6.dp)
                        .background(
                            MaterialTheme.colorScheme.onSurface.copy(alpha = .18f),
                            CircleShape
                        )
                )
            }
        }
    }
    }
}

@Composable
private fun MacroHandle(
    expanded: Boolean,
    accent: Color,
    onToggle: () -> Unit,
    onExpand: () -> Unit,
    onCollapse: () -> Unit,
    modifier: Modifier = Modifier
) {
    var dragDistance by remember { mutableStateOf(0f) }
    val shape = RoundedCornerShape(19.dp)
    Box(
        modifier = modifier
            .pointerInput(expanded) {
                detectVerticalDragGestures(
                    onDragStart = { dragDistance = 0f },
                    onVerticalDrag = { _, amount -> dragDistance += amount },
                    onDragEnd = {
                        when {
                            dragDistance < -18f -> onExpand()
                            dragDistance > 18f -> onCollapse()
                        }
                        dragDistance = 0f
                    },
                    onDragCancel = { dragDistance = 0f }
                )
            }
            .bouncyClick(onClick = onToggle)
            .shadow(8.dp, shape)
            .background(
                brush = Brush.horizontalGradient(
                    listOf(lerp(accent, Color.White, .17f), accent)
                ),
                shape = shape
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = .38f),
                shape = shape
            )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                listOf(
                    Color(0xFFFFD166),
                    Color(0xFF73D6A1),
                    Color(0xFFAAB4FF)
                ).forEach { color ->
                    Box(
                        Modifier
                            .size(6.dp)
                            .background(color, CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
private fun MacroPanel(
    currentCalories: Int,
    goalCalories: Int,
    currentProtein: Int,
    goalProtein: Int,
    currentFat: Int,
    goalFat: Int,
    currentCarbs: Int,
    goalCarbs: Int,
    accent: Color,
    dark: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = .97f),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MacroValue("К", currentCalories, goalCalories, accent)
            MacroValue(
                "Б",
                currentProtein,
                goalProtein,
                if (dark) Color(0xFF78C59C) else Color(0xFF3F8064)
            )
            MacroValue(
                "Ж",
                currentFat,
                goalFat,
                if (dark) Color(0xFFF0A06E) else Color(0xFFB16A4D)
            )
            MacroValue(
                "У",
                currentCarbs,
                goalCarbs,
                if (dark) Color(0xFFAAB4FF) else Color(0xFF6C72B7)
            )
        }
    }
}

@Composable
private fun MacroValue(
    label: String,
    current: Int,
    goal: Int,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(Modifier.size(5.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(Modifier.height(3.dp))
        Text(
            text = "$current/$goal",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun moodFor(mealType: String): DayMood = when (mealType) {
    "завтрак" -> DayMood(
        label = "Завтрак",
        accent = if (isSystemInDarkTheme()) Color(0xFFF0A06E) else BreakfastColor,
        backgroundTop = if (isSystemInDarkTheme()) Color(0xFF30231C) else Color(0xFFFFF4E9),
        backgroundBottom = if (isSystemInDarkTheme()) Color(0xFF101713) else Color(0xFFF6F7F2),
        glow = if (isSystemInDarkTheme()) Color(0xFF9B5C36) else Color(0xFFFFC786)
    )
    "ужин" -> DayMood(
        label = "Ужин",
        accent = if (isSystemInDarkTheme()) Color(0xFFAAB4FF) else DinnerColor,
        backgroundTop = if (isSystemInDarkTheme()) Color(0xFF1D2238) else Color(0xFFE9ECFA),
        backgroundBottom = if (isSystemInDarkTheme()) Color(0xFF101319) else Color(0xFFF4F5F2),
        glow = if (isSystemInDarkTheme()) Color(0xFF4E5B9E) else Color(0xFF8F9BE5)
    )
    else -> DayMood(
        label = "Обед",
        accent = if (isSystemInDarkTheme()) Color(0xFF78C59C) else LunchColor,
        backgroundTop = if (isSystemInDarkTheme()) Color(0xFF172A21) else Color(0xFFEAF5EC),
        backgroundBottom = if (isSystemInDarkTheme()) Color(0xFF101713) else Color(0xFFF5F7F1),
        glow = if (isSystemInDarkTheme()) Color(0xFF356B4E) else Color(0xFF86C99E)
    )
}
