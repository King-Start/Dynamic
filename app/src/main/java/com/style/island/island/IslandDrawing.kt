package com.style.island.island

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.style.island.data.IslandSettings

fun DrawScope.islandOutline(
    topLeft: Offset,
    size: Size,
    corner: Float,
    notch: Boolean,
    draw: DrawScope.(Outline) -> Unit,
) {
    val shape = islandShape(corner.coerceAtLeast(0f).dp, notch)
    val outline = shape.createOutline(size, layoutDirection, this)
    translate(topLeft.x, topLeft.y) { draw(outline) }
}

fun DrawScope.drawIslandRect(
    color: Color,
    topLeft: Offset,
    size: Size,
    corner: Float,
    notch: Boolean,
    style: DrawStyle = Fill,
    alpha: Float = 1f,
) = islandOutline(topLeft, size, corner, notch) { drawOutline(it, color, alpha, style) }

fun DrawScope.drawIslandRect(
    brush: androidx.compose.ui.graphics.Brush,
    topLeft: Offset,
    size: Size,
    corner: Float,
    notch: Boolean,
    style: DrawStyle = Fill,
    alpha: Float = 1f,
) = islandOutline(topLeft, size, corner, notch) { drawOutline(it, brush, alpha, style) }

fun DrawScope.drawIslandRect(
    color: Color,
    left: Float,
    top: Float,
    width: Float,
    height: Float,
    corner: Float,
    notch: Boolean,
    style: DrawStyle = Fill,
    alpha: Float = 1f,
) = drawIslandRect(color, Offset(left, top), Size(width, height), corner, notch, style, alpha)

fun DrawScope.islandGlow(color: Color, alpha: Float, corner: Float, notch: Boolean = false) {
    val spread = 10.dp.toPx()
    for (i in 1..10) {
        val d = spread * i / 10f
        drawIslandRect(color, Offset(-d, -d), Size(size.width + 2 * d, size.height + 2 * d), corner + d, notch, alpha = 0.06f * alpha)
    }
}

fun islandShape(corner: Dp, notch: Boolean): RoundedCornerShape =
    if (notch) RoundedCornerShape(0.dp, 0.dp, corner, corner) else RoundedCornerShape(corner)

fun islandShape(corner: Dp, s: IslandSettings): RoundedCornerShape = islandShape(corner, s.notch)

class AnimatedIslandShape(
    private val corner: State<Dp>,
    private val notch: Boolean,
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline =
        islandShape(corner.value, notch).createOutline(size, layoutDirection, density)
}
