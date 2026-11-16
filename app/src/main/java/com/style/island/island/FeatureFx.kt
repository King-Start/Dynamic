package com.style.island.island

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.style.island.data.FeatureAnim
import com.style.island.data.IslandSettings

/**
 * Анимация появления/исчезновения фичи. Возвращает пару enter/exit для AnimatedVisibility.
 * BOUNCE — пружинистый отскок (scale + fade), SLIDE — выезд сбоку, SCALE — пульс-зум с вращением,
 * SWIPE — шторка вверх (expand/shrink по вертикали), GLOW — проявление из свечения (fade + scale).
 */
fun featureAnim(anim: FeatureAnim): Pair<EnterTransition, ExitTransition> = when (anim) {
    FeatureAnim.BOUNCE -> {
        val spec = tween<Float>(320)
        scaleIn(initialScale = 0.5f, animationSpec = spec) + fadeIn(spec) to
            scaleOut(targetScale = 0.5f, animationSpec = tween(200)) + fadeOut(tween(200))
    }
    FeatureAnim.SLIDE -> {
        slideInHorizontally(tween(280)) { it / 3 } + fadeIn(tween(220)) to
            slideOutHorizontally(tween(200)) { it / 3 } + fadeOut(tween(160))
    }
    FeatureAnim.SCALE -> {
        scaleIn(initialScale = 0.6f, animationSpec = tween(260)) + fadeIn(tween(200)) to
            scaleOut(targetScale = 0.6f, animationSpec = tween(180)) + fadeOut(tween(160))
    }
    FeatureAnim.SWIPE -> {
        expandVertically(tween(260), expandFrom = Alignment.Top) + fadeIn(tween(200)) to
            shrinkVertically(tween(200), shrinkTowards = Alignment.Top) + fadeOut(tween(160))
    }
    FeatureAnim.GLOW -> {
        scaleIn(initialScale = 0.85f, animationSpec = tween(420)) + fadeIn(tween(420)) to
            scaleOut(targetScale = 0.85f, animationSpec = tween(260)) + fadeOut(tween(260))
    }
}

/**
 * Цвет акцента фичи: если задан [IslandSettings.featureColors] для этого [actId] — берём его,
 * иначе [fallback]. Удобно для обводки/свечения/фона пузыря.
 */
fun IslandSettings.featureColor(actId: String, fallback: Color): Color {
    val argb = featureColors[actId] ?: return fallback
    return Color(argb)
}