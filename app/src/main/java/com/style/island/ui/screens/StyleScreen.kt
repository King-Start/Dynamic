package com.style.island.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.style.island.data.IslandPreset
import com.style.island.data.IslandSettings
import com.style.island.data.NoticeDesign
import com.style.island.island.Ios
import com.style.island.island.MsgView
import com.style.island.island.NoticeDesignBody
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
import com.style.island.data.IslandLayout
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
 * «Внешний вид»: выбор стиля острова — Dynamic Island, HyperIsland, Neon, Gradient, iPhone, Aurora, Sunset, Frost.
 */
@Composable
fun StyleScreen(onBack: () -> Unit, open: (com.style.island.ui.Screen) -> Unit = {}) {
    val s by SettingsRepo.state.collectAsState()
    var styleDialog by remember { mutableStateOf(false) }
    var layoutDialog by remember { mutableStateOf(false) }
    var presetDialog by remember { mutableStateOf(false) }

    Page(tr("Внешний вид острова", "Island style", "Tampilan island"), onBack) {
        IslandPreview()

        SectionHeader(tr("Готовые темы", "Ready-made themes", "Tema siap pakai"))
        Group {
            GroupItem(
                Pos.Single, Icons.Rounded.AutoFixHigh, tr("Тема целиком", "Whole theme", "Tema sekaligus"),
                tr("Один выбор меняет стиль, раскладку и вид уведомлений сразу.",
                    "One pick changes the style, layout and notification design together.",
                    "Satu pilihan mengubah gaya, tata letak isi, dan desain notifikasi sekaligus."),
                color = Tile.Teal,
                onClick = { presetDialog = true },
            )
        }

        SectionHeader(tr("Вид уведомлений", "Notification design", "Desain notifikasi"))
        NoticeDesignGallery(s)

        SectionHeader(tr("Стиль", "Style", "Gaya"))
        Group {
            GroupItem(
                Pos.Single, Icons.Rounded.Palette, tr("Стиль острова", "Island style", "Gaya island"),
                tr("Классика, стекло, неон, градиент, аврора, закат, иней…", "Classic, glass, neon, gradient, aurora, sunset, frost…", "Klasik, kaca, neon, gradien, aurora, senja, es kristal…"),
                color = Tile.Purple, badge = s.style.title,
                onClick = { styleDialog = true },
            )
        }

        SectionHeader(tr("Раскладка", "Layout", "Tata letak"))
        Group {
            GroupItem(
                Pos.Single, Icons.Rounded.Palette, tr("Раскладка содержимого", "Content layout", "Tata letak isi"),
                tr("Классика, кольцо, с подписью, две строки, полоса или мини — для музыки, таймера, зарядки, звонков и других.",
                    "Classic, ring, labeled, two lines, bar or mini — for music, timer, charging, calls and more.",
                    "Klasik, cincin, berlabel, dua baris, garis progres, atau mungil — untuk musik, timer, pengisian, panggilan, dan lainnya."),
                color = Tile.Indigo, badge = s.layout.title,
                onClick = { layoutDialog = true },
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
            tr("Смена стиля сразу видна на острове и в превью ниже. Данные, события и жесты у всех стилей общие.", "The style change is visible on the island and in the preview below. Data, events and gestures are shared by all styles.", "Perubahan gaya langsung terlihat di island dan pratinjau di bawah. Data, peristiwa, dan gestur sama untuk semua gaya."),
            color = Ui.Sub, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }

    if (presetDialog) {
        val current = IslandPreset.entries.firstOrNull { it.style == s.style && it.layout == s.layout && it.notice == s.noticeDesign }
            ?: IslandPreset.CLASSIC
        ChoiceDialog(
            tr("Готовые темы", "Ready-made themes", "Tema siap pakai"),
            IslandPreset.entries, current, { it.title },
            { v ->
                SettingsRepo.update {
                    copy(style = v.style, layout = v.layout, noticeDesign = v.notice, featureStyles = emptyMap(), featureLayouts = emptyMap())
                }
            },
        ) { presetDialog = false }
    }

    if (layoutDialog) {
        ChoiceDialog(
            tr("Раскладка содержимого", "Content layout", "Tata letak isi"),
            IslandLayout.entries, s.layout, { it.title },
            { v -> SettingsRepo.update { copy(layout = v) } },
        ) { layoutDialog = false }
    }

    if (styleDialog) {
        ChoiceDialog(
            tr("Стиль острова", "Island style", "Gaya island"),
            IslandStyle.entries, s.style, { it.title },
            { v -> SettingsRepo.update { copy(style = v) } },
        ) { styleDialog = false }
    }
}


/** Galeri desain notifikasi: setiap kartu adalah pratinjau nyata dari desainnya; ketuk untuk memilih. */
@Composable
private fun NoticeDesignGallery(s: IslandSettings) {
    val view = MsgView(
        appName = tr("Сообщения", "Messages", "Pesan"),
        title = "Anna",
        text = tr("Встретимся в семь? Я возьму камеру.", "Meet at seven? I'll bring the camera.", "Jadi ketemu jam 7 ya? Aku bawa kameranya."),
        avatar = null,
        appIcon = null,
        fallback = Ios.bell,
        accent = Ui.Primary,
        count = 3,
        time = tr("сейчас", "now", "sekarang"),
    )
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(horizontal = 2.dp)) {
        items(NoticeDesign.entries) { d ->
            val selected = d == s.noticeDesign
            val shape = RoundedCornerShape(26.dp)
            Column(Modifier.width(272.dp).clickable { SettingsRepo.update { copy(noticeDesign = d) } }) {
                Box(
                    Modifier.fillMaxWidth().heightIn(min = 132.dp).clip(shape).background(Color.Black)
                        .border(if (selected) 2.dp else 1.dp, if (selected) Ui.Primary else Ui.Hairline, shape),
                    contentAlignment = Alignment.Center,
                ) {
                    NoticeDesignBody(d, view, s, 260.dp) {}
                }
                Text(
                    d.title, color = if (selected) Ui.Primary else Ui.Text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp, start = 4.dp),
                )
            }
        }
    }
}
