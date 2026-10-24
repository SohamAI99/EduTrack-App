package com.example.edutrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.edutrack.data.local.StudySession
import com.example.edutrack.data.local.Subject
import com.example.edutrack.data.model.SessionWithSubject
import com.example.edutrack.data.model.SubjectProgress
import com.example.edutrack.data.repository.Repository
import com.example.edutrack.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.Locale

data class DashboardUiState(
    val todayMinutes: Int = 0,
    val todayGoalMinutes: Int = 120,
    val todayProgressFraction: Float = 0f,
    val currentStreak: Int = 0,
    val subjectsProgress: List<SubjectProgress> = emptyList(),
    val recentSessions: List<SessionWithSubject> = emptyList()
)

class DashboardViewModel(
    private val repository: Repository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val subjectsFlow = repository.getAllSubjects()
    private val sessionsFlow = repository.getAllSessions()
    private val dailyGoalFlow = preferencesRepository.dailyTargetMinutes

    val uiState: StateFlow<DashboardUiState> = combine(
        subjectsFlow,
        sessionsFlow,
        dailyGoalFlow
    ) { subjects, sessions, dailyGoal ->
        val today = LocalDate.now()
        val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        // Today's total study minutes
        val todayMinutes = sessions
            .filter { it.date.isEqual(today) }
            .sumOf { it.durationMinutes }

        val todayProgress = if (dailyGoal > 0) {
            (todayMinutes.toFloat() / dailyGoal.toFloat()).coerceIn(0f, 1f)
        } else 0f

        // Calculate streak
        val streak = calculateStreak(sessions.map { it.date }.toSet(), today)

        // Subject progress this week
        val subjectsMap = subjects.associateBy { it.id }
        val subjectsProgress = subjects.map { subject ->
            val loggedMins = sessions
                .filter { it.subjectId == subject.id && !it.date.isBefore(startOfWeek) && !it.date.isAfter(today) }
                .sumOf { it.durationMinutes }
            val goalMins = subject.weeklyGoalHours * 60
            val fraction = if (goalMins > 0) loggedMins.toFloat() / goalMins.toFloat() else 0f
            val remainingHours = (goalMins - loggedMins).coerceAtLeast(0) / 60.0
            val remainingStr = if (remainingHours <= 0) "Goal reached!" else String.format(Locale.getDefault(), "%.1fh remaining", remainingHours)

            SubjectProgress(
                subject = subject,
                loggedMinutesThisWeek = loggedMins,
                goalMinutesThisWeek = goalMins,
                progressFraction = fraction,
                remainingHoursString = remainingStr
            )
        }

        // Recent 5 sessions with subject info
        val recentSessions = sessions.take(5).map { session ->
            SessionWithSubject(
                session = session,
                subject = subjectsMap[session.subjectId]
            )
        }

        DashboardUiState(
            todayMinutes = todayMinutes,
            todayGoalMinutes = dailyGoal,
            todayProgressFraction = todayProgress,
            currentStreak = streak,
            subjectsProgress = subjectsProgress,
            recentSessions = recentSessions
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    private fun calculateStreak(sessionDates: Set<LocalDate>, today: LocalDate): Int {
        if (sessionDates.isEmpty()) return 0
        var streak = 0
        var checkDate = if (sessionDates.contains(today)) today else today.minusDays(1)

        while (sessionDates.contains(checkDate)) {
            streak++
            checkDate = checkDate.minusDays(1)
        }
        return streak
    }

    companion object {
        fun provideFactory(
            repository: Repository,
            preferencesRepository: UserPreferencesRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DashboardViewModel(repository, preferencesRepository) as T
            }
        }
    }
}
