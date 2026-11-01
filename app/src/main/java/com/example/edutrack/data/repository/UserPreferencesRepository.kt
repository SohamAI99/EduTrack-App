package com.example.edutrack.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.edutrack.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val USER_NAME = stringPreferencesKey("user_name")
        val AVATAR_PATH = stringPreferencesKey("avatar_path")
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { preferences ->
        val savedMode = preferences[PreferencesKeys.THEME_MODE]
        when (savedMode) {
            ThemeMode.LIGHT.name -> ThemeMode.LIGHT
            ThemeMode.DARK.name -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
    }

    suspend fun setThemeMode(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = themeMode.name
        }
    }

    val userName: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_NAME] ?: ""
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name.trim()
        }
    }

    val avatarPath: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.AVATAR_PATH] ?: ""
    }

    suspend fun setAvatarPath(path: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AVATAR_PATH] = path.trim()
        }
    }
}
