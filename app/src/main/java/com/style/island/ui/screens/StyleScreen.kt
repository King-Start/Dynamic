package com.style.island.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.NotificationsActive
import com.style.island.shizuku.ShizukuBridge
import androidx.compose.material.icons.rounded.ViewAgenda
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.style.island.core.tr
import com.style.island.data.*
import com.style.island.island.Ios
import com.style.island.island.MsgView
import com.style.island.island.NoticeDesignBody
import com.style.island.island.PreviewScene
import com.style.island.ui.*

/**
 * Tema dan tata letak: tema siap pakai, desain notifikasi (galeri dengan pratinjau nyata),
 * gaya permukaan, dan tata letak isi island. Pratinjau langsung di atas ikut berubah.
 */
@Composable
fun StyleScreen(onBack: () -> Unit, open: (Screen) -> Unit = {}) {
    val s by SettingsRepo.state.collectAsState()
    var scene by rememberSaveable { mutableStateOf(PreviewScene.MESSAGE) }
    var presetDialog by rememberSaveable { mutableStateOf(false) }
    var styleDialog by rememberSaveable { mutableStateOf(false) }
    var layoutDialog by rememberSaveable { mutableStateOf(false) }

    Page(tr("Тема и раскладка", "Theme and layout"), onBack, preview = {
        SettingsPreview(false, scene, onScene = { scene = it })
    }) {
        SectionHeader(tr("Готовые темы", "Ready-made themes"))
        GroupItem(
            Pos.Single, Icons.Rounded.AutoFixHigh, tr("Тема целиком", "Whole theme"),
            tr("Один выбор меняет стиль, раскладку и вид уведомлений сразу.", "One pick changes the style, layout and notification design together."),
            color = Tile.Teal, onClick = { presetDialog = true },
        ) { Arrow() }

        SectionHeader(tr("Вид уведомлений", "Notification design"))
        NoticeDesignGallery(s)
        Text(
            tr("Применяется к уведомлениям в виде карточки. Ответ и кнопки работают в каждом виде.",
                "Applies to notifications shown as a card. Replies and buttons work in every design."),
            color = Ui.Sub, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 6.dp),
        )

        SectionHeader(tr("Уведомления", "Notifications"))
        Group {
            GroupItem(
                Pos.Single, Icons.Rounded.NotificationsActive,
                tr("Какие приложения показывать", "Which apps to show"),
                tr("Выбор приложений: только WhatsApp, Discord и другие — или все подряд.", "Choose apps: only WhatsApp, Discord and others — or everything."),
                color = Tile.Purple, badge = if (s.notifApps.isEmpty()) tr("Все", "All") else "${s.notifApps.size}",
                onClick = { open(Screen.APPS) },
            ) { Arrow() }
        }

        SectionHeader(tr("Стиль по фичам", "Per-feature style"))
        Group {
            GroupItem(
                Pos.Single, Icons.Rounded.Palette,
                tr("Стиль для каждой фичи", "Style per feature"),
                tr("У каждого события острова (звонок, зарядка, музыка…) свой стиль, раскладка, анимация и цвет.",
                    "Each island event (call, charging, music…) has its own style, layout, animation and colour."),
                color = Tile.Purple,
                onClick = { open(Screen.FEATURE_STYLES) },
            ) { Arrow() }
        }

        if (s.style == IslandStyle.HYPERISLAND) {
            SectionHeader(tr("Стекло HyperIsland", "HyperIsland glass"))
            Group {
                GroupToggle(
                    Pos.First, Icons.Rounded.AutoAwesome, tr("Яркая кромка", "Bright edge"),
                    tr("Тонкий светлый контур и линия по верхней грани", "Thin light outline and a line along the top edge"),
                    s.hyperHighlight, color = Tile.Pink,
                ) { v -> SettingsRepo.update { copy(hyperHighlight = v) } }
                GroupSlider(
                    Pos.Last, Icons.Rounded.LightMode, tr("Сила света", "Light strength"),
                    value = s.hyperLight, range = 0f..1f, label = "${(s.hyperLight * 100).toInt()}%",
                    steps = 9, color = Tile.Orange,
                ) { v -> SettingsRepo.update { copy(hyperLight = v) } }
            }
        }

        SectionHeader(tr("Доступ к системе", "System access"))
        Group {
            val mode = ShizukuBridge.privilegeName
            GroupItem(
                Pos.First, Icons.Rounded.DarkMode, tr("Без root", "No root"),
                tr("Обычный overlay-режим доступен всегда", "Regular overlay mode is always available"),
                color = Tile.Green,
            )
            GroupItem(
                Pos.Last, Icons.Rounded.AutoAwesome, tr("Shizuku / HyperOS", "Shizuku / HyperOS"),
                tr("Сейчас: %s. Shizuku даёт права системы без root; путь HyperIsland (LSPosed) активен на HyperOS".format(mode),
                    "Now: %s. Shizuku gives system rights without root; the HyperIsland path (LSPosed) is active on HyperOS".format(mode)),
                color = Tile.Blue, badge = if (ShizukuBridge.isXiaomi) "HyperOS" else null,
            )
        }
        if (ShizukuBridge.available && !ShizukuBridge.granted) {
            PillAction(tr("Запросить доступ Shizuku", "Request Shizuku access")) { ShizukuBridge.requestPermission() }
        }

        SectionHeader(tr("Материал и раскладка", "Material and layout"))
        Group {
            GroupItem(
                Pos.First, Icons.Rounded.Palette, tr("Стиль острова", "Island style"), s.style.title,
                color = Tile.Purple, onClick = { styleDialog = true },
            ) { Arrow() }
            GroupItem(
                Pos.Last, Icons.Rounded.ViewAgenda, tr("Раскладка содержимого", "Content layout"), s.layout.title,
                color = Tile.Indigo, onClick = { layoutDialog = true },
            ) { Arrow() }
        }
        Text(
            tr("Раскладка действует для музыки, таймера, секундомера, часов, звонков, фонарика, загрузок и зарядки.",
                "The layout applies to music, timer, stopwatch, clock, calls, flashlight, downloads and charging."),
            color = Ui.Sub, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 6.dp),
        )
    }

    if (presetDialog) {
        val current = IslandPreset.entries.firstOrNull { it.style == s.style && it.layout == s.layout && it.notice == s.noticeDesign }
            ?: IslandPreset.CLASSIC
        ChoiceDialog(
            tr("Готовые темы", "Ready-made themes"), IslandPreset.entries, current, { it.title },
            { v -> SettingsRepo.update { copy(style = v.style, layout = v.layout, noticeDesign = v.notice) } },
        ) { presetDialog = false }
    }
    if (styleDialog) {
        ChoiceDialog(
            tr("Стиль острова", "Island style"), IslandStyle.entries, s.style, { it.title },
            { v -> SettingsRepo.update { copy(style = v) } },
        ) { styleDialog = false }
    }
    if (layoutDialog) {
        ChoiceDialog(
            tr("Раскладка содержимого", "Content layout"), IslandLayout.entries, s.layout, { it.title },
            { v -> SettingsRepo.update { copy(layout = v) } },
        ) { layoutDialog = false }
    }
}

/** Galeri desain notifikasi: tiap kartu adalah pratinjau nyata desainnya; ketuk untuk memilih. */
@Composable
private fun NoticeDesignGallery(s: IslandSettings) {
    val view = MsgView(
        appName = tr("Сообщения", "Messages"),
        title = "Anna",
        text = tr("Встретимся в семь? Я возьму камеру.", "Meet at seven? I'll bring the camera."),
        avatar = null,
        appIcon = null,
        fallback = Ios.bell,
        accent = Ui.Primary,
        count = 3,
        time = tr("сейчас", "now"),
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
