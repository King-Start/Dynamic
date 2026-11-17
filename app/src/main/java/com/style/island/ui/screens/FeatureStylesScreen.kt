@file:OptIn(ExperimentalLayoutApi::class)

package com.style.island.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.FlashlightOn
import androidx.compose.material.icons.rounded.HourglassEmpty
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.style.island.core.tr
import com.style.island.data.FeatureAnim
import com.style.island.data.IslandLayout
import com.style.island.data.IslandStyle
import com.style.island.data.layoutFor
import com.style.island.data.SettingsRepo
import com.style.island.ui.Group
import com.style.island.ui.GroupItem
import com.style.island.ui.Page
import com.style.island.ui.Pos
import com.style.island.ui.SectionHeader
import com.style.island.ui.Tile
import com.style.island.ui.Ui

/** Палитра акцентных цветов для фич (ARGB). */
private val featureColors = listOf(
    0xFF34C759, 0xFF0A84FF, 0xFFFF9F0A, 0xFFFF453A, 0xFFBF5AF2, 0xFFFF2D92,
    0xFF64D2FF, 0xFF30D158, 0xFFFFD60A, 0xFF5E5CE6, 0xFFFF375F, 0xFF8E8E93,
)

/**
 * Экран: для каждого типа активности (фичи) — свой стиль, своя анимация и свой цвет.
 * Нажатие на фичу открывает окно с тремя разделами.
 */
@Composable
fun FeatureStylesScreen(onBack: () -> Unit) {
    val s by SettingsRepo.state.collectAsState()
    var editKey by remember { mutableStateOf<String?>(null) }

    val features = listOf(
        FeatureInfo("media", tr("Музыка", "Music", "Musik"), Icons.Rounded.LibraryMusic, Tile.Purple),
        FeatureInfo("timer", tr("Таймер", "Timer", "Timer"), Icons.Rounded.Timer, Tile.Orange),
        FeatureInfo("stopwatch", tr("Секундомер", "Stopwatch", "Stopwatch"), Icons.Rounded.HourglassEmpty, Tile.Orange),
        FeatureInfo("torch", tr("Фонарик", "Flashlight", "Senter"), Icons.Rounded.FlashlightOn, Tile.Yellow),
        FeatureInfo("charging", tr("Зарядка", "Charging", "Pengisian"), Icons.Rounded.BatteryChargingFull, Tile.Green),
        FeatureInfo("notice", tr("Уведомления", "Notifications", "Notifikasi"), Icons.Rounded.Notifications, Tile.Blue),
        FeatureInfo("call", tr("Звонки", "Calls", "Panggilan"), Icons.Rounded.Call, Tile.Green),
        FeatureInfo("alarm", tr("Будильник", "Alarm", "Alarm"), Icons.Rounded.Lock, Tile.Red),
        FeatureInfo("chrono", tr("Таймеры Часов", "Clock timers", "Timer Jam"), Icons.Rounded.MonitorHeart, Tile.Indigo),
        FeatureInfo("progress", tr("Прогресс", "Progress", "Progres"), Icons.Rounded.SwapVert, Tile.Pink),
        FeatureInfo("live", tr("Live активности", "Live activities", "Aktivitas Live"), Icons.Rounded.Wifi, Tile.Teal),
        FeatureInfo("pay", tr("Оплата", "Payment", "Pembayaran"), Icons.Rounded.PowerSettingsNew, Tile.Purple),
    )

    Page(tr("Стиль по фичам", "Per-feature style", "Gaya per fitur"), onBack,
        subtitle = tr(
            "У каждого события острова (звонок, зарядка, музыка…) теперь свой стиль, анимация и цвет. Нажмите на фичу.",
            "Each island event (call, charging, music…) now has its own style, animation and color. Tap a feature.",
            "Setiap event island (panggilan, pengisian, musik…) kini punya gaya, animasi, dan warna sendiri. Tekan fitur.",
        )) {
        SectionHeader(tr("Стиль, анимация и цвет", "Style, animation & color", "Gaya, animasi & warna"))
        Group {
            features.forEachIndexed { idx, f ->
                val style = s.featureStyles[f.key] ?: s.style
                val anim = s.featureAnims[f.key]
                val color = s.featureColors[f.key]
                val layout = s.layoutFor(f.key)
                val subtitle = buildString {
                    append(style.title)
                    if (layout != IslandLayout.CLASSIC) append("  ·  " + layout.title)
                    if (anim != null) append("  ·  " + anim.title)
                    if (color != null) append("  ●")
                }
                GroupItem(
                    pos = if (idx == 0) Pos.First else if (idx == features.lastIndex) Pos.Last else Pos.Middle,
                    icon = f.icon,
                    title = f.title,
                    subtitle = subtitle,
                    color = f.color,
                    onClick = { editKey = f.key },
                ) { Arrow() }
            }
        }
    }

    val key = editKey
    if (key != null) {
        val f = features.find { it.key == key }
        FeatureEditDialog(
            title = f?.title ?: key,
            style = s.featureStyles[key] ?: s.style,
            anim = s.featureAnims[key],
            color = s.featureColors[key],
            layout = s.layoutFor(key),
            onLayout = { l -> SettingsRepo.update { copy(featureLayouts = featureLayouts + (key to l)) } },
            onStyle = { SettingsRepo.update { copy(featureStyles = featureStyles + (key to it)) } },
            onAnim = { a -> SettingsRepo.update { copy(featureAnims = if (a == null) featureAnims - key else featureAnims + (key to a)) } },
            onColor = { c -> SettingsRepo.update { copy(featureColors = if (c == null) featureColors - key else featureColors + (key to c)) } },
            onDismiss = { editKey = null },
        )
    }
}

/** Окно настройки одной фичи: стиль + анимация + цвет. */
@Composable
private fun FeatureEditDialog(
    title: String,
    style: IslandStyle,
    anim: FeatureAnim?,
    color: Int?,
    layout: IslandLayout,
    onLayout: (IslandLayout) -> Unit,
    onStyle: (IslandStyle) -> Unit,
    onAnim: (FeatureAnim?) -> Unit,
    onColor: (Int?) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Ui.Card,
        titleContentColor = Ui.Text,
        title = { Text(title, fontWeight = FontWeight.SemiBold) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                // --- Стиль ---
                Text(tr("Стиль", "Style", "Gaya"), color = Ui.Sub, fontSize = 13.sp, modifier = Modifier.padding(bottom = 4.dp))
                IslandStyle.entries.forEach { st ->
                    RadioRow(st.title, st == style) { onStyle(st); onDismiss() }
                }
                // --- Tata letak ---
                Spacer(Modifier.size(10.dp))
                Text(tr("Раскладка", "Layout", "Tata letak"), color = Ui.Sub, fontSize = 13.sp, modifier = Modifier.padding(bottom = 4.dp))
                IslandLayout.entries.forEach { l ->
                    RadioRow(l.title, l == layout) { onLayout(l); onDismiss() }
                }
                // --- Анимация ---
                Spacer(Modifier.size(10.dp))
                Text(tr("Анимация", "Animation", "Animasi"), color = Ui.Sub, fontSize = 13.sp, modifier = Modifier.padding(bottom = 4.dp))
                RadioRow(tr("По умолчанию", "Default", "Default"), anim == null) { onAnim(null) }
                FeatureAnim.entries.forEach { a ->
                    RadioRow(a.title, a == anim) { onAnim(a); onDismiss() }
                }
                // --- Цвет ---
                Spacer(Modifier.size(10.dp))
                Text(tr("Цвет акцента", "Accent color", "Warna aksen"), color = Ui.Sub, fontSize = 13.sp, modifier = Modifier.padding(bottom = 4.dp))
                ColorRow(featureColors, color, onPick = onColor, onDismiss = onDismiss)
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(tr("Готово", "Done", "Selesai"), color = Ui.Primary) } },
    )
}

@Composable
private fun RadioRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = null,
            colors = RadioButtonDefaults.colors(selectedColor = Ui.Primary, unselectedColor = Ui.Outline))
        Spacer(Modifier.width(10.dp))
        Text(label, color = Ui.Text, fontSize = 15.sp)
    }
}

@Composable
private fun ColorRow(colors: List<Long>, selected: Int?, onPick: (Int?) -> Unit, onDismiss: () -> Unit) {
    Column {
        FlowRow(
            Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            colors.forEach { c ->
                val argb = c.toInt()
                val isSel = selected == argb
                Box(
                    Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(c))
                        .then(if (isSel) Modifier.border(2.dp, Ui.Primary, CircleShape) else Modifier)
                        .clickable { onPick(if (isSel) null else argb); onDismiss() },
                )
            }
        }
        // «Default» — сброс цвета фичи
        TextButton(onClick = { onPick(null); onDismiss() }) {
            Text(tr("Цвет по умолчанию", "Default color", "Warna default"), color = Ui.Primary, fontSize = 13.sp)
        }
    }
}

private data class FeatureInfo(
    val key: String,
    val title: String,
    val icon: ImageVector,
    val color: Color,
)