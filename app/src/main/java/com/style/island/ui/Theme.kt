package com.style.island.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Tema gelap netral dengan satu aksen teal, mengikuti logo aplikasi. */
object Ui {
    val Bg = Color(0xFF0D0E11)
    val Card = Color(0xFF16171B)
    val IconBg = Color(0xFF202228)
    val Primary = Color(0xFF3DD6C6)
    val OnPrimary = Color(0xFF04201D)
    val Text = Color(0xFFECEDEF)
    val Sub = Color(0xFF8E929B)
    val Divider = Color(0xFF2A2D33)
    val Nav = Color(0xFF1A1C21)
    val Pink = Color(0xFFFBDDF4)
    val Outline = Color(0xFF5C616B)

    /** Garis tipis di tepi kartu dan panel */
    val Hairline = Color(0x14FFFFFF)
}

/** Warna ikon per kategori: sengaja diredam agar tidak norak; dipakai sebagai latar tipis dan warna ikon. */
object Tile {
    val Blue = Color(0xFF5B9CF6)
    val Green = Color(0xFF4CBF7F)
    val Orange = Color(0xFFEBA04A)
    val Red = Color(0xFFEA6A62)
    val Purple = Color(0xFFA582EE)
    val Pink = Color(0xFFE76C9E)
    val Teal = Color(0xFF3DD6C6)
    val Indigo = Color(0xFF7782EE)
    val Yellow = Color(0xFFDDB444)
    val Gray = Color(0xFF8A8F99)
}

@Composable
fun IslandTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Ui.Primary,
            onPrimary = Ui.OnPrimary,
            secondary = Ui.Primary,
            background = Ui.Bg,
            onBackground = Ui.Text,
            surface = Ui.Card,
            onSurface = Ui.Text,
            surfaceVariant = Ui.IconBg,
            onSurfaceVariant = Ui.Sub,
            surfaceContainerHigh = Ui.Card,
            outline = Ui.Outline,
        ),
        typography = InterTypography,
        content = content,
    )
}
