package com.akira.island.island

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Утилиты рисования формы острова: свечение, контур, прямоугольник формы.
 * Вынесены в отдельный файл, чтобы быть доступными для Egg.kt, PayFx.kt, IslandOverlay.kt и др.
 */

/** Прямоугольник той же формы, что остров: у «Как вырез» верхние углы прямые. */
fun DrawScope.drawIslandRect(
    color: Color,
    topLeft: Offset,
    size: Size,
    corner: Float,
    notch: Boolean,
    style: DrawStyle = DrawStyle.Fill,
    alpha: Float = 1f,
) = islandOutline(topLeft, size, corner, notch) { drawOutline(it, color, alpha, style) }

/** Вторая перегрузка: прямо по лево/верх/ширина/высота. */
fun DrawScope.drawIslandRect(
    color: Color,
    left: Float,
    top: Float,
    width: Float,
    height: Float,
    corner: Float,
    notch: Boolean,
    style: DrawStyle = DrawStyle.Fill,
    alpha: Float = 1f,
) = drawIslandRect(color, Offset(left, top), Size(width, height), corner, notch, style, alpha)

/** Контур формы острова со скруглением [corner] в [topLeft] — для свечения, волн и кромки вокруг него. */
fun DrawScope.islandOutline(topLeft: Offset, size: Size, corner: Float, notch: Boolean, draw: DrawScope.(androidx.compose.ui.graphics.Outline) -> Unit) {
    val outline = islandShape(corner.coerceAtLeast(0f).toDp(), notch).createOutline(size, layoutDirection, this)
    translate(topLeft.x, topLeft.y) { draw(outline) }
}

/**
 * Свечение вокруг острова: десять колец, всё прозрачнее наружу, — мягкий ореол без размытия, в запасе окна.
 * У «Как вырез» верх прямой, как у самого острова.
 */
fun DrawScope.islandGlow(color: Color, alpha: Float, corner: Float, notch: Boolean = false) {
    val spread = 10.dp.toPx()
    for (i in 1..10) {
        val d = spread * i / 10f
        drawIslandRect(
            color,
            Offset(-d, -d),
            Size(size.width + 2 * d, size.height + 2 * d),
            corner + d,
            notch,
            alpha = 0.06f * alpha,
        )
    }
}

/** Форма острова: капсула со скруглениями или, у «Как вырез», прямой верх у края экрана. */
fun islandShape(corner: androidx.compose.ui.unit.Dp, s: IslandSettings): androidx.compose.ui.graphics.Shape =
    islandShape(corner, s.notch)

/** Форма острова: капсула со скруглениями или, у «Как вырез», прямой верх у края экрана. */
fun islandShape(corner: androidx.compose.ui.unit.Dp, notch: Boolean): androidx.compose.ui.graphics.Shape =
    if (notch) androidx.compose.foundation.shape.RoundedCornerShape(0.dp, 0.dp, corner, corner)
    else androidx.compose.foundation.shape.RoundedCornerShape(corner)

/** Форма острова со скруглением из анимации: рамка и стекло читают его при рисовании и обновляются без пересборки. */
class AnimatedIslandShape(
    private val corner: androidx.compose.runtime.State<androidx.compose.ui.unit.Dp>,
    private val notch: Boolean,
) : androidx.compose.ui.graphics.Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: androidx.compose.ui.text.TextDirection,
        density: androidx.compose.ui.unit.Density,
    ): androidx.compose.ui.graphics.Outline =
        islandShape(corner.value, notch).createOutline(size, layoutDirection, density)
}