package com.zeneyestudio.zplate.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zeneyestudio.zplate.R
import com.zeneyestudio.zplate.ui.theme.BreakfastColor
import com.zeneyestudio.zplate.ui.theme.DinnerColor
import com.zeneyestudio.zplate.ui.theme.LunchColor

data class MealVisual(
    val accent: Color,
    val background: Color,
    @DrawableRes val artwork: Int
)

@Composable
fun mealVisual(type: String): MealVisual {
    val dark = isSystemInDarkTheme()
    return when (type) {
        "завтрак" -> MealVisual(
            if (dark) Color(0xFFF0A06E) else BreakfastColor,
            if (dark) Color(0xFF32241D) else Color(0xFFFFF2E7),
            R.drawable.illustration_breakfast
        )
        "ужин" -> MealVisual(
            if (dark) Color(0xFFAAB4FF) else DinnerColor,
            if (dark) Color(0xFF20243A) else Color(0xFFECEFFA),
            R.drawable.illustration_dinner
        )
        else -> MealVisual(
            if (dark) Color(0xFF78C59C) else LunchColor,
            if (dark) Color(0xFF182D24) else Color(0xFFEAF4EC),
            R.drawable.illustration_lunch
        )
    }
}

@Composable
fun WellnessBackdrop(
    accent: Color = MaterialTheme.colorScheme.primary,
    topColor: Color = MaterialTheme.colorScheme.background,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.background(
            Brush.verticalGradient(
                listOf(topColor, MaterialTheme.colorScheme.background)
            )
        )
    ) {
        val surfaceColor = MaterialTheme.colorScheme.surface
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                color = accent.copy(alpha = .09f),
                radius = size.width * .66f,
                center = Offset(size.width * .08f, size.height * .12f)
            )
            drawCircle(
                color = surfaceColor.copy(alpha = .34f),
                radius = size.width * .48f,
                center = Offset(size.width * .98f, size.height * .8f)
            )
        }
        content()
    }
}

@Composable
fun MealArtwork(
    mealType: String,
    size: Dp,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(mealVisual(mealType).artwork),
        contentDescription = null,
        modifier = modifier.size(size),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun OnboardingArtwork(size: Dp, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.illustration_onboarding),
        contentDescription = null,
        modifier = modifier.size(size),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun Modifier.bouncyClick(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) .965f else 1f,
        animationSpec = tween(120),
        label = "press feedback"
    )
    return this
        .scale(scale)
        .clickable(
            interactionSource = interaction,
            indication = null,
            enabled = enabled,
            role = Role.Button,
            onClick = onClick
        )
}

@Composable
fun WellnessCard(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surface.copy(alpha = .96f),
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(26.dp),
        color = color,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp
    ) {
        Box(contentAlignment = Alignment.Center, content = content)
    }
}
