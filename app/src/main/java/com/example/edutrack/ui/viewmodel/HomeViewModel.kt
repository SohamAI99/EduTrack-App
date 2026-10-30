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
import java.time.LocalDate
import java.util.Locale

data class SubjectProgressUiModel(
    val subject: Subject,
    val spentMinutesThisWeek: Int,
    val goalHours: Int,
    val progressFraction: Float,
    val percentageInt: Int,
    val hoursVsGoalText: String,
    val remainingText: String
)

data class HomeUiState(
    val hasSubjects: Boolean = false,
    val todayTotalMinutes: Int = 0,
    val weeklyTotalMinutes: Int = 0,
    val weeklyGoalMinutes: Int = 0,
    val subjects: List<SubjectProgressUiModel> = emptyList(),
    val recentSessions: List<SessionItemUiModel> = emptyList()
)

class HomeViewModel(private val repository: Repository) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getAllSessions(),
        repository.getAllSubjects()
    ) { sessions, subjects ->
        val today = LocalDate.now()
        val monday = today.minusDays((today.dayOfWeek.value - 1).toLong())
        val sunday = monday.plusDays(6)

        val todayTotalMinutes = sessions
            .filter { it.date == today }
            .sumOf { it.durationMinutes }

        val thisWeekSessions = sessions.filter { it.date >= monday && it.date <= sunday }
        val weeklyTotalMinutes = thisWeekSessions.sumOf { it.durationMinutes }

        val totalWeeklyGoalHours = subjects.sumOf { it.weeklyGoalHours }
        val weeklyGoalMinutes = totalWeeklyGoalHours * 60

        val subjectMap = subjects.associateBy { it.id }
        val subjectSessionsMap = thisWeekSessions.groupBy { it.subjectId }

        val subjectProgressList = subjects.map { subject ->
            val spentMinutes = subjectSessionsMap[subject.id]?.sumOf { it.durationMinutes } ?: 0
            val goalMins = subject.weeklyGoalHours * 60
            val fraction = if (goalMins > 0) (spentMinutes.toFloat() / goalMins).coerceAtMost(1f) else 0f
            val percent = if (goalMins > 0) ((spentMinutes.toDouble() / goalMins) * 100).toInt() else 0

            SubjectProgressUiModel(
                subject = subject,
                spentMinutesThisWeek = spentMinutes,
                goalHours = subject.weeklyGoalHours,
                progressFraction = fraction,
                percentageInt = percent,
                hoursVsGoalText = formatHoursSpentVsGoal(spentMinutes, subject.weeklyGoalHours),
                remainingText = formatRemainingText(spentMinutes, subject.weeklyGoalHours)
            )
        }

        val recentSessionsList = sessions.take(3).map { session ->
            val subject = subjectMap[session.subjectId]
            SessionItemUiModel(
                id = session.id,
                subjectId = session.subjectId,
                subjectName = subject?.name ?: "Unknown Subject",
                subjectColorHex = subject?.colorHex ?: "#6C5CE7",
                durationMinutes = session.durationMinutes,
                date = session.date,
                notes = session.notes
            )
        }

        HomeUiState(
            hasSubjects = subjects.isNotEmpty(),
            todayTotalMinutes = todayTotalMinutes,
            weeklyTotalMinutes = weeklyTotalMinutes,
            weeklyGoalMinutes = weeklyGoalMinutes,
            subjects = subjectProgressList,
            recentSessions = recentSessionsList
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    companion object {
        fun provideFactory(repository: Repository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(repository) as T
                }
            }
    }
}

fun formatDurationCompact(minutes: Int): String {
    if (minutes <= 0) return "0m"
    val hrs = minutes / 60
    val mins = minutes % 60
    return when {
        hrs > 0 && mins > 0 -> "${hrs}h ${mins}m"
        hrs > 0 -> "${hrs}h"
        else -> "${mins}m"
    }
}

fun formatHoursSpentVsGoal(spentMinutes: Int, goalHours: Int): String {
    val spentHours = spentMinutes / 60.0
    val spentFormatted = if (spentHours % 1.0 == 0.0) "${spentHours.toInt()}h" else String.format(Locale.getDefault(), "%.1fh", spentHours)
    return "$spentFormatted of ${goalHours}h goal"
}

fun formatRemainingText(spentMinutes: Int, goalHours: Int): String {
    val goalMinutes = goalHours * 60
    val remaining = goalMinutes - spentMinutes
    if (remaining <= 0) return "Goal reached"
    val hrs = remaining / 60
    val mins = remaining % 60
    return when {
        hrs > 0 && mins > 0 -> "${hrs}h ${mins}m remaining"
        hrs > 0 -> "${hrs}h remaining"
        else -> "${mins}m remaining"
    }
}
