package com.style.island.island

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.toArgb
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

/** Geser hue warna aksen (untuk gaya multi-warna) dan jaga agar tetap cerah. */
private fun shiftHue(c: Color, deg: Float): Color {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(c.toArgb(), hsv)
    hsv[0] = (hsv[0] + deg + 360f) % 360f
    hsv[1] = hsv[1].coerceAtLeast(0.45f)
    hsv[2] = hsv[2].coerceAtLeast(0.85f)
    return Color(android.graphics.Color.HSVToColor(hsv))
}

/**
 * «Aurora»: badan gelap dengan dua cahaya lembut berwarna (aksen dan warna turunannya)
 * yang bertemu seperti aurora, plus tepi gradien tiga warna.
 */
fun Modifier.auroraSurface(shape: Shape, color: Color): Modifier = drawWithCache {
    val outline = shape.createOutline(size, layoutDirection, this)
    val dp = 1.dp.toPx()
    val w = size.width
    val h = size.height
    val c2 = shiftHue(color, 70f)
    val c3 = shiftHue(color, -60f)

    val glowA = Brush.radialGradient(
        listOf(color.copy(alpha = 0.60f), Color.Transparent),
        center = Offset(w * 0.12f, h * 0.95f),
        radius = w * 0.60f,
    )
    val glowB = Brush.radialGradient(
        listOf(c2.copy(alpha = 0.50f), Color.Transparent),
        center = Offset(w * 0.88f, h * 0.05f),
        radius = w * 0.55f,
    )
    val rim = Brush.linearGradient(
        listOf(color.copy(alpha = 0.95f), c2.copy(alpha = 0.95f), c3.copy(alpha = 0.95f)),
        start = Offset(0f, 0f),
        end = Offset(w, h),
    )

    onDrawWithContent {
        drawRect(Color(0xFF07060D))
        drawRect(glowA)
        drawRect(glowB)
        drawContent()
        drawOutline(outline, rim, style = Stroke(width = 1.4f * dp))
    }
}

/**
 * «Senja»: gradien hangat jingga → magenta → ungu tua, dengan bayangan lembut di bawah
 * agar teks putih tetap terbaca. Warna tetap, tidak mengikuti aksen.
 */
fun Modifier.sunsetSurface(shape: Shape): Modifier = drawWithCache {
    val outline = shape.createOutline(size, layoutDirection, this)
    val dp = 1.dp.toPx()
    val w = size.width
    val h = size.height

    val body = Brush.linearGradient(
        0f to Color(0xFFFF8A3D),
        0.50f to Color(0xFFD6336C),
        1f to Color(0xFF2A0E45),
        start = Offset(0f, 0f),
        end = Offset(w, h),
    )
    val shade = Brush.verticalGradient(
        0f to Color.Transparent,
        1f to Color.Black.copy(alpha = 0.30f),
    )

    onDrawWithContent {
        drawRect(body)
        drawRect(shade)
        drawContent()
        drawOutline(outline, Color.White.copy(alpha = 0.22f), style = Stroke(width = 1.2f * dp))
    }
}

/**
 * «Es Kristal»: kaca biru gelap setengah bening dengan kilau diagonal dan tepi putih tipis,
 * seperti panel kaca beku.
 */
fun Modifier.frostSurface(shape: Shape): Modifier = drawWithCache {
    val outline = shape.createOutline(size, layoutDirection, this)
    val dp = 1.dp.toPx()
    val w = size.width
    val h = size.height

    val body = Brush.verticalGradient(
        0f to Color(0xFF16263D).copy(alpha = 0.90f),
        1f to Color(0xFF0A1220).copy(alpha = 0.94f),
    )
    val sheen = Brush.linearGradient(
        0f to Color.White.copy(alpha = 0.20f),
        0.45f to Color.White.copy(alpha = 0.04f),
        1f to Color.Transparent,
        start = Offset(0f, 0f),
        end = Offset(w, h),
    )
    val rim = Brush.linearGradient(
        0f to Color.White.copy(alpha = 0.60f),
        0.5f to Color.White.copy(alpha = 0.12f),
        1f to Color.White.copy(alpha = 0.35f),
        start = Offset(0f, 0f),
        end = Offset(w, h),
    )

    onDrawWithContent {
        drawRect(body)
        drawRect(sheen)
        drawContent()
        drawOutline(outline, rim, style = Stroke(width = 1.2f * dp))
    }
}
