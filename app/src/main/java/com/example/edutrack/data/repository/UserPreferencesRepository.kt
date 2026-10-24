package com.example.edutrack.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppThemeMode {
    LIGHT, DARK, SYSTEM
}

class UserPreferencesRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("edutrack_user_prefs", Context.MODE_PRIVATE)

    private val _userName = MutableStateFlow(prefs.getString(KEY_USER_NAME, "Soham") ?: "Soham")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userAvatar = MutableStateFlow(prefs.getString(KEY_USER_AVATAR, "🎓") ?: "🎓")
    val userAvatar: StateFlow<String> = _userAvatar.asStateFlow()

    private val _isOnboardingCompleted =
        MutableStateFlow(prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false))
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted.asStateFlow()

    private val _themeMode = MutableStateFlow(
        try {
            AppThemeMode.valueOf(prefs.getString(KEY_THEME_MODE, AppThemeMode.DARK.name) ?: AppThemeMode.DARK.name)
        } catch (_: Exception) {
            AppThemeMode.DARK
        }
    )
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _dailyTargetMinutes = MutableStateFlow(prefs.getInt(KEY_DAILY_TARGET_MINUTES, 120))
    val dailyTargetMinutes: StateFlow<Int> = _dailyTargetMinutes.asStateFlow()

    fun setUserName(name: String) {
        prefs.edit().putString(KEY_USER_NAME, name).apply()
        _userName.value = name
    }

    fun setUserAvatar(avatar: String) {
        prefs.edit().putString(KEY_USER_AVATAR, avatar).apply()
        _userAvatar.value = avatar
    }

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
        _isOnboardingCompleted.value = completed
    }

    fun setThemeMode(mode: AppThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
        _themeMode.value = mode
    }

    fun setDailyTargetMinutes(minutes: Int) {
        prefs.edit().putInt(KEY_DAILY_TARGET_MINUTES, minutes).apply()
        _dailyTargetMinutes.value = minutes
    }

    companion object {
        private const val KEY_USER_NAME = "key_user_name"
        private const val KEY_USER_AVATAR = "key_user_avatar"
        private const val KEY_ONBOARDING_COMPLETED = "key_onboarding_completed"
        private const val KEY_THEME_MODE = "key_theme_mode"
        private const val KEY_DAILY_TARGET_MINUTES = "key_daily_target_minutes"
    }
}
