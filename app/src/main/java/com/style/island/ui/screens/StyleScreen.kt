package com.style.island.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.style.island.core.tr
import com.style.island.data.IslandStyle
import com.style.island.data.SettingsRepo
import com.style.island.shizuku.ShizukuBridge
import com.style.island.ui.ChoiceDialog
import com.style.island.ui.Group
import com.style.island.ui.GroupItem
import com.style.island.ui.GroupSlider
import com.style.island.ui.GroupToggle
import com.style.island.ui.Page
import com.style.island.ui.Pos
import com.style.island.ui.SectionHeader
import com.style.island.ui.Tile
import com.style.island.ui.Ui

/**
 * «Внешний вид»: выбор одного из двух стилей острова.
 *  - Dynamic Island — исходная капсула (Akira);
 *  - HyperIsland — стиль HyperIsland (тёмное «стекло» с направленным светом и яркой кромкой).
 */
@Composable
fun StyleScreen(onBack: () -> Unit, open: (com.style.island.ui.Screen) -> Unit = {}) {
    val s by SettingsRepo.state.collectAsState()
    var styleDialog by remember { mutableStateOf(false) }

    Page(tr("Внешний вид острова", "Island style", "Tampilan island"), onBack) {
        IslandPreview()

        SectionHeader(tr("Стиль", "Style", "Gaya"))
        Group {
            GroupItem(
                Pos.Single, Icons.Rounded.Palette, tr("Стиль острова", "Island style", "Gaya island"),
                tr("Dynamic Island — исходная капсула. HyperIsland — «стекло» в стиле HyperOS", "Dynamic Island is the original capsule. HyperIsland is HyperOS-style glass", "Dynamic Island — kapsul asli. HyperIsland — kaca gaya HyperOS"),
                color = Tile.Purple, badge = s.style.title,
                onClick = { styleDialog = true },
            )
        }

        SectionHeader(tr("Уведомления", "Notifications", "Notifikasi"))
        Group {
            GroupItem(
                Pos.Single, Icons.Rounded.NotificationsActive,
                tr("Какие приложения показывать", "Which apps to show", "Aplikasi mana yang ditampilkan"),
                tr("Выбор приложений: только WhatsApp, Discord и другие — или все подряд.", "Choose apps: only WhatsApp, Discord and others — or everything.", "Pilih aplikasi: hanya WhatsApp, Discord, dan lainnya — atau semuanya."),
                color = Tile.Purple, badge = if (s.notifApps.isEmpty()) tr("Все", "All", "Semua") else "${s.notifApps.size}",
                onClick = { open(com.style.island.ui.Screen.APPS) },
            )
        }

        SectionHeader(tr("Стиль по фичам", "Per-feature style", "Gaya per fitur"))
        Group {
            GroupItem(
                Pos.Single, Icons.Rounded.Palette,
                tr("Стиль для каждой фичи", "Style per feature", "Gaya per fitur"),
                tr("У каждого события острова (звонок, зарядка, музыка…) теперь свой стиль.",
                    "Each island event (call, charging, music…) now has its own style.",
                    "Setiap event island (panggilan, pengisian, musik…) kini punya gaya sendiri."),
                color = Tile.Purple,
                onClick = { open(com.style.island.ui.Screen.FEATURE_STYLES) },
            )
        }

        if (s.style == IslandStyle.HYPERISLAND) {
            SectionHeader(tr("Стекло HyperIsland", "HyperIsland glass", "Kaca HyperIsland"))
            Group {
                GroupToggle(
                    Pos.First, Icons.Rounded.AutoAwesome, tr("Яркая кромка", "Bright edge", "Tepi terang"),
                    tr("Тонкий светлый контур и линия по верхней грани", "Thin light outline and a line along the top edge", "Garis tipis terang dan garis di tepi atas"),
                    s.hyperHighlight, color = Tile.Pink,
                ) { v -> SettingsRepo.update { copy(hyperHighlight = v) } }
                GroupSlider(
                    Pos.Last, Icons.Rounded.LightMode, tr("Сила света", "Light strength", "Kekuatan cahaya"),
                    value = s.hyperLight, range = 0f..1f, label = "${(s.hyperLight * 100).toInt()}%",
                    steps = 9, color = Tile.Orange,
                ) { v -> SettingsRepo.update { copy(hyperLight = v) } }
            }
        }

        SectionHeader(tr("Доступ к системе", "System access", "Akses sistem"))
        Group {
            val mode = ShizukuBridge.privilegeName
            GroupItem(
                Pos.First, Icons.Rounded.DarkMode, tr("Без root", "No root", "Tanpa root"),
                tr("Обычный overlay-режим доступен всегда", "Regular overlay mode is always available", "Mode overlay biasa selalu tersedia"),
                color = Tile.Green,
            )
            GroupItem(
                Pos.Last, Icons.Rounded.AutoAwesome, tr("Shizuku / HyperOS", "Shizuku / HyperOS", "Shizuku / HyperOS"),
                tr("Сейчас: %s. Shizuku даёт права системы без root; путь HyperIsland (LSPosed) активен на HyperOS".format(mode),
                    "Now: %s. Shizuku gives system rights without root; the HyperIsland path (LSPosed) is active on HyperOS".format(mode)),
                color = Tile.Blue, badge = if (ShizukuBridge.isXiaomi) "HyperOS" else null,
            )
        }

        if (ShizukuBridge.available && !ShizukuBridge.granted) {
            com.style.island.ui.PillAction(tr("Запросить доступ Shizuku", "Request Shizuku access", "Minta akses Shizuku")) {
                ShizukuBridge.requestPermission()
            }
        }

        Text(
            tr("Смена стиля сразу видна на острове и в превью ниже. Данные, события и жесты у обоих стилей общие.", "The style change is visible on the island and in the preview below. Data, events and gestures are shared by both styles.", "Perubahan gaya langsung terlihat di island dan pratinjau di bawah. Data, peristiwa, dan gestur sama untuk kedua gaya."),
            color = Ui.Sub, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }

    if (styleDialog) {
        ChoiceDialog(
            tr("Стиль острова", "Island style", "Gaya island"),
            IslandStyle.entries, s.style, { it.title },
            { v -> SettingsRepo.update { copy(style = v) } },
        ) { styleDialog = false }
    }
}
