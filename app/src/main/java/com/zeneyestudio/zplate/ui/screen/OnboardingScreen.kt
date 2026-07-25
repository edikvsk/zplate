package com.zeneyestudio.zplate.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zeneyestudio.zplate.ui.components.MealArtwork
import com.zeneyestudio.zplate.ui.components.OnboardingArtwork
import com.zeneyestudio.zplate.ui.components.WellnessBackdrop
import com.zeneyestudio.zplate.ui.components.WellnessCard
import com.zeneyestudio.zplate.ui.components.bouncyClick
import com.zeneyestudio.zplate.ui.components.mealVisual
import com.zeneyestudio.zplate.util.TimeHelper
import java.util.Locale

@Composable
fun OnboardingScreen(
    onComplete: (
        breakfast: TimeHelper.TimeRange,
        lunch: TimeHelper.TimeRange,
        dinner: TimeHelper.TimeRange
    ) -> Unit
) {
    var step by remember { mutableIntStateOf(0) }
    var breakfastStart by remember { mutableIntStateOf(7 * 60) }
    var breakfastEnd by remember { mutableIntStateOf(12 * 60) }
    var lunchStart by remember { mutableIntStateOf(12 * 60) }
    var lunchEnd by remember { mutableIntStateOf(16 * 60) }
    var dinnerStart by remember { mutableIntStateOf(16 * 60) }
    var dinnerEnd by remember { mutableIntStateOf(22 * 60) }

    val mealType = when (step) {
        1 -> "завтрак"
        2 -> "обед"
        else -> "ужин"
    }
    val visual = mealVisual(mealType)

    WellnessBackdrop(
        accent = if (step == 0) MaterialTheme.colorScheme.primary else visual.accent,
        topColor = if (step == 0) MaterialTheme.colorScheme.primaryContainer else visual.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (step > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier
                            .size(44.dp)
                            .bouncyClick { step-- },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = .96f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.ArrowBack, contentDescription = "Назад")
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        repeat(3) { index ->
                            Box(
                                Modifier
                                    .size(if (index == step - 1) 22.dp else 7.dp, 7.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (index == step - 1) visual.accent
                                        else visual.accent.copy(alpha = .2f)
                                    )
                            )
                        }
                    }
                    Spacer(Modifier.size(44.dp))
                }
            }

            AnimatedContent(
                targetState = step,
                modifier = Modifier.weight(1f),
                transitionSpec = {
                    val direction = if (targetState > initialState) {
                        AnimatedContentTransitionScope.SlideDirection.Left
                    } else {
                        AnimatedContentTransitionScope.SlideDirection.Right
                    }
                    (slideIntoContainer(direction, tween(360)) + fadeIn(tween(240)))
                        .togetherWith(
                            slideOutOfContainer(direction, tween(360)) + fadeOut(tween(180))
                        )
                },
                label = "onboarding step"
            ) { current ->
                if (current == 0) {
                    WelcomeContent()
                } else {
                    val start = when (current) {
                        1 -> breakfastStart
                        2 -> lunchStart
                        else -> dinnerStart
                    }
                    val end = when (current) {
                        1 -> breakfastEnd
                        2 -> lunchEnd
                        else -> dinnerEnd
                    }
                    TimeContent(
                        mealType = mealType,
                        startMinutes = start,
                        endMinutes = end,
                        onStartChange = {
                            when (current) {
                                1 -> breakfastStart = it
                                2 -> lunchStart = it
                                else -> dinnerStart = it
                            }
                        },
                        onEndChange = {
                            when (current) {
                                1 -> breakfastEnd = it
                                2 -> lunchEnd = it
                                else -> dinnerEnd = it
                            }
                        }
                    )
                }
            }

            val buttonColor = if (step == 0) MaterialTheme.colorScheme.primary else visual.accent
            val buttonContent = if (isSystemInDarkTheme()) {
                MaterialTheme.colorScheme.background
            } else {
                Color.White
            }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .bouncyClick {
                        if (step < 3) {
                            step++
                        } else {
                            onComplete(
                                breakfastStart.toRange(breakfastEnd),
                                lunchStart.toRange(lunchEnd),
                                dinnerStart.toRange(dinnerEnd)
                            )
                        }
                    },
                shape = RoundedCornerShape(20.dp),
                color = buttonColor,
                shadowElevation = 8.dp
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (step == 0) "Начать" else if (step < 3) "Продолжить" else "Готово",
                        style = MaterialTheme.typography.titleMedium,
                        color = buttonContent
                    )
                    Spacer(Modifier.size(9.dp))
                    Icon(
                        imageVector = if (step == 3) Icons.Rounded.Check
                        else Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null,
                        tint = buttonContent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomeContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OnboardingArtwork(size = 270.dp)
        Spacer(Modifier.height(18.dp))
        Text(
            text = "Ваш ритм питания",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Спокойно следите за рационом\nбез лишних цифр и сложных экранов",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TimeContent(
    mealType: String,
    startMinutes: Int,
    endMinutes: Int,
    onStartChange: (Int) -> Unit,
    onEndChange: (Int) -> Unit
) {
    val visual = mealVisual(mealType)
    val title = mealType.replaceFirstChar { it.titlecase(Locale.getDefault()) }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MealArtwork(mealType = mealType, size = 150.dp)
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(5.dp))
        Text(
            text = "Когда обычно?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(22.dp))

        WellnessCard(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = .96f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimeControl(
                    label = "с",
                    minutes = startMinutes,
                    accent = visual.accent,
                    onChange = onStartChange,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    Modifier
                        .size(24.dp, 2.dp)
                        .background(visual.accent.copy(alpha = .28f))
                )
                TimeControl(
                    label = "до",
                    minutes = endMinutes,
                    accent = visual.accent,
                    onChange = onEndChange,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TimeControl(
    label: String,
    minutes: Int,
    accent: Color,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onChange((minutes - 15 + 1440) % 1440) }) {
                Icon(
                    Icons.Rounded.Remove,
                    contentDescription = "Уменьшить на 15 минут",
                    tint = accent
                )
            }
            Text(
                text = minutes.asTime(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = { onChange((minutes + 15) % 1440) }) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = "Увеличить на 15 минут",
                    tint = accent
                )
            }
        }
    }
}

private fun Int.asTime(): String = String.format(Locale.getDefault(), "%02d:%02d", this / 60, this % 60)

private fun Int.toRange(endMinutes: Int) = TimeHelper.TimeRange(
    startHour = this / 60,
    startMinute = this % 60,
    endHour = endMinutes / 60,
    endMinute = endMinutes % 60
)
