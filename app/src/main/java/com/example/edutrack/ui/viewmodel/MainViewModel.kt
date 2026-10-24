package com.example.edutrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.edutrack.data.local.SeedData
import com.example.edutrack.data.repository.AppThemeMode
import com.example.edutrack.data.repository.Repository
import com.example.edutrack.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val repository: Repository
) : ViewModel() {

    val themeMode: StateFlow<AppThemeMode> = preferencesRepository.themeMode
    val userName: StateFlow<String> = preferencesRepository.userName
    val userAvatar: StateFlow<String> = preferencesRepository.userAvatar
    val isOnboardingCompleted: StateFlow<Boolean> = preferencesRepository.isOnboardingCompleted

    private val _isAppearanceSheetVisible = MutableStateFlow(false)
    val isAppearanceSheetVisible: StateFlow<Boolean> = _isAppearanceSheetVisible.asStateFlow()

    init {
        viewModelScope.launch {
            SeedData.populateIfEmpty(repository)
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        preferencesRepository.setThemeMode(mode)
    }

    fun setUserName(name: String) {
        preferencesRepository.setUserName(name)
    }

    fun setUserAvatar(avatar: String) {
        preferencesRepository.setUserAvatar(avatar)
    }

    fun completeOnboarding() {
        preferencesRepository.setOnboardingCompleted(true)
    }

    fun showAppearanceSheet(show: Boolean) {
        _isAppearanceSheetVisible.value = show
    }

    companion object {
        fun provideFactory(
            preferencesRepository: UserPreferencesRepository,
            repository: Repository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainViewModel(preferencesRepository, repository) as T
            }
        }
    }
}
