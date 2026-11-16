package com.akira.island.island

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Дополнительные внешние виды острова: Неон, Градиент, iPhone.
 * Каждый рисуется слоями поверх базового фона — преломления настоящего стекла Android не даёт.
 */

/**
 * «Неон»: почти чёрное тело с цветной светящейся кромкой.
 * [color] — цвет свечения (берётся из выбранного акцента острова). Свечение имитируется несколькими
 * всё более широкими и прозрачными обводками.
 */
fun Modifier.neonSurface(shape: Shape, color: Color, glow: Boolean = true): Modifier = drawWithCache {
    val outline = shape.createOutline(size, layoutDirection, this)
    val dp = 1.dp.toPx()
    val c = color

    // Тело — тёмное, чуть подсвечено цветом у краёв
    val body = Brush.verticalGradient(
        0f to c.copy(alpha = 0.22f),
        0.45f to Color(0xFF0E0E12).copy(alpha = 0.92f),
        1f to c.copy(alpha = 0.16f),
    )

    onDrawWithContent {
        drawRect(body)
        drawContent()

        if (glow) {
            // Внешнее свечение: широкие полупрозрачные обводки
            drawOutline(outline, c.copy(alpha = 0.10f), style = Stroke(width = 8f * dp))
            drawOutline(outline, c.copy(alpha = 0.22f), style = Stroke(width = 4f * dp))
            drawOutline(outline, c.copy(alpha = 0.55f), style = Stroke(width = 2.2f * dp))
            // Яркая тонкая линия по краю
            drawOutline(outline, c.copy(alpha = 0.95f), style = Stroke(width = 1.2f * dp))
        } else {
            drawOutline(outline, c.copy(alpha = 0.35f), style = Stroke(width = 1.4f * dp))
        }
    }
}

/**
 * «Градиент»: тело залито градиентом от цвета акцента (сверху) к тёмному (снизу), тонкая светлая кромка.
 */
fun Modifier.gradientSurface(shape: Shape, color: Color): Modifier = drawWithCache {
    val outline = shape.createOutline(size, layoutDirection, this)
    val dp = 1.dp.toPx()

    val body = Brush.verticalGradient(
        0f to color.copy(alpha = 0.85f),
        0.5f to color.copy(alpha = 0.45f).compositeOver(Color(0xFF121016)),
        1f to Color(0xFF0E0E12).copy(alpha = 0.95f),
    )
    val sheen = Brush.linearGradient(
        0f to Color.White.copy(alpha = 0.16f),
        0.5f to Color.Transparent,
        1f to Color.White.copy(alpha = 0.06f),
        start = Offset(0f, 0f),
        end = Offset(size.width, size.height),
    )

    onDrawWithContent {
        drawRect(body)
        drawRect(sheen)
        drawContent()
        drawOutline(outline, Color.White.copy(alpha = 0.20f), style = Stroke(width = 1.2f * dp))
    }
}

/**
 * «iPhone»: чистое чёрное тело с очень тонкой нейтральной кромкой — минимализм как у Dynamic Island на iPhone.
 */
fun Modifier.iphoneSurface(shape: Shape): Modifier = drawWithCache {
    val outline = shape.createOutline(size, layoutDirection, this)
    val dp = 1.dp.toPx()

    onDrawWithContent {
        drawRect(Color(0xFF000000).copy(alpha = 0.96f))
        drawContent()
        drawOutline(outline, Color.White.copy(alpha = 0.10f), style = Stroke(width = 1f * dp))
    }
}