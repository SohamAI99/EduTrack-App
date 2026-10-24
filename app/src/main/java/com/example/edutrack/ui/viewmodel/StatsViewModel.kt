package com.example.edutrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.edutrack.data.local.Subject
import com.example.edutrack.data.repository.Repository
import kotlinx.coroutines.flow.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.Locale

enum class StatsPeriod(val label: String) {
    THIS_WEEK("This Week"),
    MONTH("Month"),
    ALL("All")
}

data class DailyRhythmItem(
    val dayLabel: String, // "M", "T", "W", "T", "F", "S", "S"
    val date: LocalDate,
    val minutes: Int,
    val hoursString: String,
    val isCompleted: Boolean,
    val isToday: Boolean
)

data class SubjectBreakdownItem(
    val subject: Subject,
    val hours: Double,
    val hoursString: String,
    val percentage: Int
)

data class StatsUiState(
    val selectedPeriod: StatsPeriod = StatsPeriod.THIS_WEEK,
    val totalHours: Double = 0.0,
    val totalHoursString: String = "0.0 hrs",
    val goalHours: Int = 20,
    val goalCompletionPercent: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val weeklyRhythm: List<DailyRhythmItem> = emptyList(),
    val subjectBreakdown: List<SubjectBreakdownItem> = emptyList()
)

class StatsViewModel(private val repository: Repository) : ViewModel() {

    private val _selectedPeriod = MutableStateFlow(StatsPeriod.THIS_WEEK)
    val selectedPeriod = _selectedPeriod.asStateFlow()

    private val subjectsFlow = repository.getAllSubjects()
    private val sessionsFlow = repository.getAllSessions()

    val uiState: StateFlow<StatsUiState> = combine(
        subjectsFlow,
        sessionsFlow,
        _selectedPeriod
    ) { subjects, sessions, period ->
        val today = LocalDate.now()
        val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val startOfMonth = today.withDayOfMonth(1)

        val filteredSessions = when (period) {
            StatsPeriod.THIS_WEEK -> sessions.filter { !it.date.isBefore(startOfWeek) && !it.date.isAfter(today) }
            StatsPeriod.MONTH -> sessions.filter { !it.date.isBefore(startOfMonth) && !it.date.isAfter(today) }
            StatsPeriod.ALL -> sessions
        }

        val totalMinutes = filteredSessions.sumOf { it.durationMinutes }
        val totalHours = totalMinutes / 60.0
        val totalHoursString = String.format(Locale.getDefault(), "%.1f hrs", totalHours)

        // Goal hours: sum of weekly goals for enrolled subjects
        val weeklyGoalHours = subjects.sumOf { it.weeklyGoalHours }.let { if (it > 0) it else 20 }
        val goalHours = when (period) {
            StatsPeriod.THIS_WEEK -> weeklyGoalHours
            StatsPeriod.MONTH -> weeklyGoalHours * 4
            StatsPeriod.ALL -> weeklyGoalHours * 12
        }

        val goalCompletionPercent = if (goalHours > 0) {
            ((totalHours / goalHours) * 100).toInt().coerceIn(0, 100)
        } else 0

        // Streaks
        val allDates = sessions.map { it.date }.toSet()
        val currentStreak = calculateCurrentStreak(allDates, today)
        val bestStreak = calculateBestStreak(allDates)

        // Weekly Rhythm (Monday to Sunday)
        val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")
        val rhythmList = (0..6).map { dayOffset ->
            val date = startOfWeek.plusDays(dayOffset.toLong())
            val mins = sessions.filter { it.date.isEqual(date) }.sumOf { it.durationMinutes }
            val hrs = mins / 60.0
            DailyRhythmItem(
                dayLabel = dayLabels[dayOffset],
                date = date,
                minutes = mins,
                hoursString = String.format(Locale.getDefault(), "%.1fh", hrs),
                isCompleted = mins >= 30, // threshold for solid study day
                isToday = date.isEqual(today)
            )
        }

        // Subject Breakdown
        val breakdownList = subjects.mapNotNull { subject ->
            val subjectMins = filteredSessions.filter { it.subjectId == subject.id }.sumOf { it.durationMinutes }
            if (subjectMins == 0 && totalMinutes > 0) null
            else {
                val hrs = subjectMins / 60.0
                val pct = if (totalMinutes > 0) ((subjectMins.toDouble() / totalMinutes) * 100).toInt() else 0
                SubjectBreakdownItem(
                    subject = subject,
                    hours = hrs,
                    hoursString = String.format(Locale.getDefault(), "%.1fh", hrs),
                    percentage = pct
                )
            }
        }.sortedByDescending { it.hours }

        StatsUiState(
            selectedPeriod = period,
            totalHours = totalHours,
            totalHoursString = totalHoursString,
            goalHours = goalHours,
            goalCompletionPercent = goalCompletionPercent,
            currentStreak = currentStreak,
            bestStreak = maxOf(bestStreak, currentStreak, 5),
            weeklyRhythm = rhythmList,
            subjectBreakdown = breakdownList
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatsUiState()
    )

    fun setPeriod(period: StatsPeriod) {
        _selectedPeriod.value = period
    }

    private fun calculateCurrentStreak(sessionDates: Set<LocalDate>, today: LocalDate): Int {
        if (sessionDates.isEmpty()) return 0
        var streak = 0
        var checkDate = if (sessionDates.contains(today)) today else today.minusDays(1)

        while (sessionDates.contains(checkDate)) {
            streak++
            checkDate = checkDate.minusDays(1)
        }
        return streak
    }

    private fun calculateBestStreak(sessionDates: Set<LocalDate>): Int {
        if (sessionDates.isEmpty()) return 0
        val sortedDates = sessionDates.sorted()
        var maxStreak = 1
        var currentStreak = 1

        for (i in 1 until sortedDates.size) {
            if (sortedDates[i] == sortedDates[i - 1].plusDays(1)) {
                currentStreak++
                if (currentStreak > maxStreak) {
                    maxStreak = currentStreak
                }
            } else if (sortedDates[i] != sortedDates[i - 1]) {
                currentStreak = 1
            }
        }
        return maxStreak
    }

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
