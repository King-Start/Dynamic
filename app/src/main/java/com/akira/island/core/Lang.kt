package com.akira.island.core

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.platform.LocalConfiguration
import com.akira.island.data.AppLanguage
import com.akira.island.data.SettingsRepo
import java.util.Locale

/**
 * Язык приложения и острова: русский или английский. «Как в системе» — русский, если телефон на русском,
 * иначе английский. Строки пишутся парой прямо в коде: tr("Зарядка", "Charging").
 */
object Lang {
    /** Активный язык приложения: RU / EN / ID. */
    val current: AppLanguage
        get() = when (SettingsRepo.current.language) {
            AppLanguage.RU -> AppLanguage.RU
            AppLanguage.EN -> AppLanguage.EN
            AppLanguage.ID -> AppLanguage.ID
            AppLanguage.SYSTEM -> when (systemLanguage()) {
                "ru" -> AppLanguage.RU
                "id" -> AppLanguage.ID
                else -> AppLanguage.EN
            }
        }

    val en: Boolean get() = current != AppLanguage.RU
    val id: Boolean get() = current == AppLanguage.ID

    /** Для дат: «пятница, 24 октября» или «Friday, October 24» или «Jumat, 24 Oktober» */
    val locale: Locale
        get() = when (current) {
            AppLanguage.RU -> Locale.forLanguageTag("ru")
            AppLanguage.ID -> Locale.forLanguageTag("id")
            else -> Locale.ENGLISH
        }

    private fun systemLanguage(): String =
        runCatching { Resources.getSystem().configuration.locales[0].language }.getOrNull() ?: Locale.getDefault().language
}

/** Строка на текущем языке. Третий аргумент — индонезийский; если не задан, берём английский. */
fun tr(ru: String, en: String, id: String = en): String = when (Lang.current) {
    AppLanguage.RU -> ru
    AppLanguage.ID -> id
    else -> en
}

/**
 * Сменили язык (в настройках или в системе) — всё внутри рисуется заново уже на новом языке.
 * Состояние снаружи (открытая вкладка и экран) остаётся.
 */
@Composable
fun LangScope(content: @Composable () -> Unit) {
    val s by SettingsRepo.state.collectAsState()
    // Язык системы меняется вместе с конфигурацией — чтение подписывает на неё
    LocalConfiguration.current
    key(s.language, Lang.en) { content() }
}
