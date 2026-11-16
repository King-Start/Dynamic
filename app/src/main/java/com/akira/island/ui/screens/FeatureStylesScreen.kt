package com.akira.island.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Battery
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.FlashlightOn
import androidx.compose.material.icons.rounded.HourglassEmpty
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akira.island.core.tr
import com.akira.island.data.IslandSettings
import com.akira.island.data.IslandStyle
import com.akira.island.data.SettingsRepo
import com.akira.island.ui.Page
import com.akira.island.ui.Group
import com.akira.island.ui.GroupItem
import com.akira.island.ui.Pos
import com.akira.island.ui.SectionHeader
import com.akira.island.ui.Tile
import com.akira.island.ui.ChoiceDialog

/** Экран: выбор стиля для каждого типа активности (фичи) отдельно. */
@Composable
fun FeatureStylesScreen(onBack: () -> Unit) {
    val s by SettingsRepo.state.collectAsState()
    var styleDialogKey by remember { mutableStateOf<String?>(null) }

    val features = listOf(
        FeatureInfo("media", tr("Музыка", "Music", "Musik"), Icons.Rounded.LibraryMusic, Tile.Purple),
        FeatureInfo("timer", tr("Таймер", "Timer", "Timer"), Icons.Rounded.Timer, Tile.Orange),
        FeatureInfo("stopwatch", tr("Секундомер", "Stopwatch", "Stopwatch"), Icons.Rounded.HourglassEmpty, Tile.Orange),
        FeatureInfo("torch", tr("Фонарик", "Flashlight", "Senter"), Icons.Rounded.FlashlightOn, Tile.Yellow),
        FeatureInfo("charging", tr("Зарядка", "Charging", "Pengisian"), Icons.Rounded.Battery, Tile.Green),
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
            "У каждого типа события острова (звонок, зарядка, музыка…) теперь свой стиль. Выбирайте ниже.",
            "Each island event type (call, charging, music…) now has its own style. Pick below.",
            "Setiap tipe event island (panggilan, pengisian, musik…) kini punya gaya sendiri. Pilih di bawah."
        )) {
        SectionHeader(tr("Стиль для каждой фичи", "Style per feature", "Gaya per fitur"))
        Group {
            features.forEachIndexed { idx, f ->
                val currentStyle = s.featureStyles[f.key] ?: s.style
                GroupItem(
                    pos = if (idx == 0) Pos.First else if (idx == features.lastIndex) Pos.Last else Pos.Middle,
                    icon = f.icon,
                    title = f.title,
                    subtitle = currentStyle.title,
                    color = f.color,
                    onClick = { styleDialogKey = f.key },
                ) { Arrow() }
            }
        }
    }

    if (styleDialogKey != null) {
        val current = s.featureStyles[styleDialogKey!!] ?: s.style
        ChoiceDialog(
            tr("Выбор стиля для %s", "Choose style for %s", "Pilih gaya untuk %s").format(
                features.find { it.key == styleDialogKey }?.title ?: styleDialogKey!!
            ),
            IslandStyle.entries, current, { it.title },
            { v -> SettingsRepo.update { copy(featureStyles = featureStyles + (styleDialogKey!! to v)) } }
        ) { styleDialogKey = null }
    }
}

private data class FeatureInfo(
    val key: String,
    val title: String,
    val icon: androidx.compose.material.icons.ImageVector,
    val color: Color,
)

private val Ui = com.akira.island.ui.Ui