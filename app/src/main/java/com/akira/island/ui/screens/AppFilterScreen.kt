package com.akira.island.ui.screens

import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akira.island.core.tr
import com.akira.island.data.SettingsRepo
import com.akira.island.ui.IslandSwitch
import com.akira.island.ui.Page
import com.akira.island.ui.Ui
import com.akira.island.ui.Group
import com.akira.island.ui.GroupToggle
import com.akira.island.ui.Pos
import com.akira.island.ui.SectionHeader
import com.akira.island.ui.Tile
import androidx.compose.material.icons.rounded.Checklist

/** Список пользовательских приложений для фильтра уведомлений острова. */
private data class AppInfo(val pkg: String, val label: String)

/** «Уведомления» → выбор приложений: какие приложения остров показывает, а какие игнорирует. */
@Composable
fun AppFilterScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val s by SettingsRepo.state.collectAsState()
    var query by remember { mutableStateOf("") }

    val apps = remember {
        val pm = context.packageManager
        val list = mutableListOf<AppInfo>()
        runCatching {
            val intent = android.content.Intent(android.content.Intent.ACTION_MAIN).addCategory(android.content.Intent.CATEGORY_LAUNCHER)
            for (ri in pm.queryIntentActivities(intent, 0)) {
                val pkg = ri.activityInfo.packageName
                if (pkg == context.packageName) continue
                val label = runCatching { ri.loadLabel(pm).toString() }.getOrDefault(pkg)
                list += AppInfo(pkg, label)
            }
        }
        list.distinctBy { it.pkg }.sortedBy { it.label.lowercase() }
    }

    val selected = s.notifApps
    val filtered = if (query.isBlank()) apps else apps.filter {
        it.label.contains(query, ignoreCase = true) || it.pkg.contains(query, ignoreCase = true)
    }

    Page(tr("Выбор приложений", "App selection", "Pilihan aplikasi"), onBack,
        subtitle = tr("Какие приложения показывают уведомления на острове. Пустой список — показывать все.", "Which apps show notifications on the island. An empty list shows all apps.", "Aplikasi mana yang menampilkan notifikasi di island. Daftar kosong berarti menampilkan semua.")) {
        SectionHeader(tr("Режим", "Mode", "Mode"))
        Group {
            GroupToggle(
                Pos.Single, Icons.Rounded.Checklist,
                tr("Только выбранные приложения", "Selected apps only", "Hanya aplikasi terpilih"),
                tr("Выкл — показывать уведомления всех приложений. Вкл — только отмеченных ниже.", "Off — show notifications from all apps. On — only from those checked below.", "Mati — tampilkan notifikasi semua aplikasi. Aktif — hanya yang dicentang di bawah."),
                selected.isNotEmpty(), color = Tile.Purple,
            ) { v ->
                if (v) {
                    SettingsRepo.update { copy(notifApps = notifApps + (apps.firstOrNull()?.pkg ?: "").takeIf { it.isNotEmpty() }.orEmpty()) }
                } else {
                    SettingsRepo.update { copy(notifApps = emptySet()) }
                }
            }
        }

        SectionHeader(tr("Приложения (${filtered.size})", "Apps (${filtered.size})"))
        if (selected.isNotEmpty()) {
            Text(
                tr("Выбрано: %d. Приложения ниже можно включать и выключать одним нажатием.", "Selected: %d. Toggle apps below with one tap.", "Dipilih: %d. Aplikasi di bawah bisa diaktifkan/dinonaktifkan dengan satu ketukan.").format(selected.size),
                color = Ui.Sub, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }

        // Поиск
        TextField(
            value = query, onValueChange = { query = it },
            placeholder = { Text(tr("Поиск приложения…", "Search apps…", "Cari aplikasi…"), color = Ui.Sub) },
            leadingIcon = { Icon(Icons.Rounded.Search, null, tint = Ui.Sub) },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Ui.Card, unfocusedContainerColor = Ui.Card,
                focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Tile.Purple,
            ),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp).clip(RoundedCornerShape(16.dp)),
        )

        // Список в обычном Column: Page уже скроллится, LazyColumn внутри дал бы бесконечную высоту и краш
        Column(modifier = Modifier.fillMaxWidth()) {
            for (app in filtered) {
                val on = app.pkg in selected
                val pm = context.packageManager
                val icon = remember(app.pkg) {
                    runCatching { pm.getApplicationIcon(app.pkg).toBitmap(96, 96).asImageBitmap() }.getOrNull()
                }
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                        .clickable { toggleApp(app.pkg, !on) }
                        .background(if (on) Ui.Card else Color.Transparent)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (icon != null) {
                        Image(icon, null, Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop)
                    } else {
                        Box(Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Ui.Card),
                            contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.Apps, null, tint = Ui.Sub, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        app.label, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium,
                        maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f),
                    )
                    Spacer(Modifier.width(8.dp))
                    IslandSwitch(checked = on, onChange = { v -> toggleApp(app.pkg, v) })
                }
                Spacer(Modifier.height(2.dp))
            }
        }
    }
}

private fun toggleApp(pkg: String, newOn: Boolean) {
    SettingsRepo.update {
        copy(notifApps = if (newOn) notifApps + pkg else notifApps - pkg)
    }
}
