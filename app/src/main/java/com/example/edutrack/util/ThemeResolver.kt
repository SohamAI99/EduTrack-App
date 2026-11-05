package com.example.edutrack.util

import com.example.edutrack.model.ThemeMode

/**
 * Resolves whether dark theme should be applied based on [ThemeMode] and system dark theme setting.
 */
fun resolveDarkTheme(mode: ThemeMode, systemDark: Boolean): Boolean {
    return when (mode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> systemDark
    }
}
