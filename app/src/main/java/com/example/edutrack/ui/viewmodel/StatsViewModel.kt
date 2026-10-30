package com.example.edutrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.edutrack.data.local.Subject
import com.example.edutrack.data.repository.Repository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Locale

data class SubjectHoursUiModel(
    val subject: Subject,
    val thisWeekHours: Double,
    val lastWeekHours: Double
)

data class RhythmDayUiModel(
    val dayOfWeek: DayOfWeek,
    val dayLabel: String,
    val date: LocalDate,
    val hasSession: Boolean,
    val isToday: Boolean,
    val isFuture: Boolean
)

data class StatsUiState(
    val hasStats: Boolean = false,
    val thisWeekTotalHours: Double = 0.0,
    val lastWeekTotalHours: Double = 0.0,
    val comparisonHoursText: String = "0.0 hrs vs last week",
    val subjectHours: List<SubjectHoursUiModel> = emptyList(),
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val rhythmDays: List<RhythmDayUiModel> = emptyList()
)

class StatsViewModel(private val repository: Repository) : ViewModel() {

    val uiState: StateFlow<StatsUiState> = combine(
        repository.getAllSessions(),
        repository.getAllSubjects()
    ) { sessions, subjects ->
        if (subjects.isEmpty() || sessions.isEmpty()) {
            StatsUiState(hasStats = false)
        } else {
            val today = LocalDate.now()
            val monday = today.minusDays((today.dayOfWeek.value - 1).toLong())
            val sunday = monday.plusDays(6)

            val lastWeekMonday = monday.minusDays(7)
            val lastWeekSunday = monday.minusDays(1)

            val thisWeekSessions = sessions.filter { it.date >= monday && it.date <= sunday }
            val lastWeekSessions = sessions.filter { it.date >= lastWeekMonday && it.date <= lastWeekSunday }

            val thisWeekTotalMins = thisWeekSessions.sumOf { it.durationMinutes }
            val lastWeekTotalMins = lastWeekSessions.sumOf { it.durationMinutes }

            val thisWeekTotalHours = thisWeekTotalMins / 60.0
            val lastWeekTotalHours = lastWeekTotalMins / 60.0
            val diffHours = thisWeekTotalHours - lastWeekTotalHours

            val comparisonText = when {
                diffHours > 0 -> String.format(Locale.getDefault(), "+%.1f hrs vs last week", diffHours)
                diffHours < 0 -> String.format(Locale.getDefault(), "%.1f hrs vs last week", diffHours)
                else -> "0.0 hrs vs last week"
            }

            StatsUiState(
                hasStats = true,
                thisWeekTotalHours = thisWeekTotalHours,
                lastWeekTotalHours = lastWeekTotalHours,
                comparisonHoursText = comparisonText
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatsUiState()
    )

    companion object {
        fun provideFactory(repository: Repository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return StatsViewModel(repository) as T
                }
            }
    }
}
