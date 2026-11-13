package com.akira.island.island

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Внешний вид острова в стиле HyperIsland.
 *
 * HyperIsland рисует «стекло» из нескольких слоёв: затемнённое полупрозрачное тело с лёгким градиентом,
 * направленный свет (сверху-слева), яркую линзу-кромку по краю и мягкую тень внутрь. Настоящего
 * преломления того, что лежит под окном, Android поверх чужого окна не даёт — поэтому всё, что видно
 * у настоящего стекла, рисуется слоями, как и в [glassSurface].
 *
 * Отличие от [glassSurface] (стиль Dynamic Island): тело заметно темнее и плотнее, свет идёт одним
 * направленным пятном сверху, кромка тоньше и ярче, а по центру верхней грани — тонкая светлая линия
 * (как у HyperIsland вокруг выреза камеры).
 */
fun Modifier.hyperSurface(
    shape: Shape,
    base: Color,
    opacity: Float,
    lightIntensity: Float = 1f,
    highlight: Boolean = true,
): Modifier = drawWithCache {
    val w = size.width
    val h = size.height
    val a = opacity.coerceIn(0.2f, 1f)
    val outline = shape.createOutline(size, layoutDirection, this)
    val dp = 1.dp.toPx()

    // Тело: почти чёрное, чуть светлее к верху — как тёмное стекло HyperIsland
    val body = Brush.verticalGradient(
        0f to base.copy(alpha = a * 0.92f),
        0.55f to base.copy(alpha = a * 0.86f),
        1f to base.copy(alpha = a * 0.95f),
    )
    // Направленный свет сверху-слева: одно яркое пятно, мягко тает
    val light = Brush.radialGradient(
        listOf(
            Color.White.copy(alpha = 0.14f * lightIntensity),
            Color.White.copy(alpha = 0.03f * lightIntensity),
            Color.Transparent,
        ),
        center = Offset(w * 0.30f, -h * 0.25f),
        radius = w * 0.95f,
    )
    // Тонкая яркая кромка-линза: светлее в верхнем левом и нижнем правом углу
    val rim = Brush.linearGradient(
        0f to Color.White.copy(alpha = 0.55f),
        0.35f to Color.White.copy(alpha = 0.16f),
        0.65f to Color.White.copy(alpha = 0.10f),
        1f to Color.White.copy(alpha = 0.40f),
        start = Offset(0f, 0f),
        end = Offset(w, h),
    )

    onDrawWithContent {
        drawRect(body)
        drawRect(light)

        drawContent()

        // Кромка: тонкий яркий контур + мягкое свечение внутрь
        if (highlight) {
            drawOutline(outline, Color.White.copy(alpha = 0.22f), style = Stroke(width = 1.2f * dp))
            drawOutline(outline, rim, alpha = 0.55f, style = Stroke(width = 1.6f * dp))
            drawOutline(outline, Color.White.copy(alpha = 0.05f), style = Stroke(width = 6f * dp))
        } else {
            drawOutline(outline, Color.White.copy(alpha = 0.16f), style = Stroke(width = 1.4f * dp))
        }

        // Верхняя светлая линия — акцент HyperIsland
        if (highlight && h > 4 * dp) {
            drawOutline(
                outline,
                Brush.horizontalGradient(
                    0.2f to Color.Transparent,
                    0.5f to Color.White.copy(alpha = 0.28f),
                    0.8f to Color.Transparent,
                ),
                alpha = 0.8f,
                style = Stroke(width = 1.0f * dp),
            )
        }
    }
}

/**
 * Прозрачная подложка стиля HyperIsland для карточек и кнопок внутри острова:
 * лёгкое затемнение вместо сплошной заливки, чтобы содержимое читалось на стекле.
 */
fun Modifier.hyperCard(color: Color = Color.White, alpha: Float = 0.10f): Modifier =
    drawWithCache {
        val outline = androidx.compose.ui.graphics.RectangleShape.createOutline(size, layoutDirection, this)
        onDrawWithContent {
            drawRect(color.copy(alpha = alpha))
            drawContent()
        }
    }
