package com.example.edutrack

import com.example.edutrack.model.ThemeMode
import com.example.edutrack.util.resolveDarkTheme
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeResolverTest {

    @Test
    fun resolveDarkTheme_lightMode_systemDarkFalse_returnsFalse() {
        assertFalse(resolveDarkTheme(ThemeMode.LIGHT, systemDark = false))
    }

    @Test
    fun resolveDarkTheme_lightMode_systemDarkTrue_returnsFalse() {
        assertFalse(resolveDarkTheme(ThemeMode.LIGHT, systemDark = true))
    }

    @Test
    fun resolveDarkTheme_darkMode_systemDarkFalse_returnsTrue() {
        assertTrue(resolveDarkTheme(ThemeMode.DARK, systemDark = false))
    }

    @Test
    fun resolveDarkTheme_darkMode_systemDarkTrue_returnsTrue() {
        assertTrue(resolveDarkTheme(ThemeMode.DARK, systemDark = true))
    }

    @Test
    fun resolveDarkTheme_systemMode_systemDarkFalse_returnsFalse() {
        assertFalse(resolveDarkTheme(ThemeMode.SYSTEM, systemDark = false))
    }

    @Test
    fun resolveDarkTheme_systemMode_systemDarkTrue_returnsTrue() {
        assertTrue(resolveDarkTheme(ThemeMode.SYSTEM, systemDark = true))
    }
}
