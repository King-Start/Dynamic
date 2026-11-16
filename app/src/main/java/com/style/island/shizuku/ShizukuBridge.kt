package com.style.island.shizuku

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import rikka.shizuku.Shizuku

/**
 * Мост к Shizuku: даёт права системы без root.
 *
 * Приложение работает и без Shizuku (обычный overlay-режим). Когда Shizuku доступен и разрешение
 * получено, открываются дополнительные возможности (запуск действий с системными правами,
 * управление процессами и т.п.). Все обращения обёрнуты в runCatching, чтобы отсутствие Shizuku
 * или отклонённое разрешение не ломали остров.
 */
object ShizukuBridge {

    private const val REQUEST_CODE = 1001

    /** Установлен ли Shizuku и жив ли его биндер. */
    val available: Boolean
        get() = runCatching { Shizuku.pingBinder() }.getOrDefault(false)

    /** Разрешение Shizuku уже выдано приложению. */
    val granted: Boolean
        get() = runCatching {
            Shizuku.isPreV11() || Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        }.getOrDefault(false)

    /** Разрешение запрошено у пользователя (иначе — молча не мешаем). */
    fun requestPermission() {
        runCatching {
            if (available && !granted) Shizuku.requestPermission(REQUEST_CODE)
        }
    }

    /**
     * Есть ли root на устройстве (для справки в разделе «HyperOS»).
     * Не требует реального запуска su — проверяет наличие бинаря su.
     */
    val rootAvailable: Boolean
        get() = runCatching {
            val paths = listOf(
                "/system/bin/su", "/system/xbin/su", "/sbin/su",
                "/system/app/Superuser.apk", "/system/xbin/magisk",
            )
            paths.any { java.io.File(it).exists() }
        }.getOrDefault(false)

    /**
     * Похоже ли устройство на HyperOS/MIUI (Xiaomi) — там доступен путь HyperIsland.
     * Определяем по бренду/производителю.
     */
    val isXiaomi: Boolean
        get() = runCatching {
            val b = Build.BRAND.lowercase() + Build.MANUFACTURER.lowercase()
            "xiaomi" in b || "redmi" in b || "poco" in b
        }.getOrDefault(false)

    /** Человекочитаемое имя поставщика прав: Shizuku или Root. */
    val privilegeName: String
        get() = when {
            granted -> "Shizuku"
            rootAvailable -> "Root (su)"
            else -> "Нет"
        }

    /** Запустить [block] с правами системы, если Shizuku дал их. */
    inline fun <T> withPrivilege(block: () -> T): T? =
        if (granted) runCatching { block() }.getOrNull() else null
}
