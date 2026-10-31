package com.example.edutrack.util

import java.time.LocalDate

/**
 * Result representing the current study streak and all-time best streak in days.
 */
data class StreakResult(
    val currentStreak: Int,
    val bestStreak: Int
)

/**
 * Pure Kotlin utility for calculating study streaks.
 * Evaluates consecutive study days without external Android dependencies.
 */
object StreakCalculator {

    /**
     * Calculates current and best study streaks from a set of study session dates.
     *
     * @param sessionDates Set of unique dates on which study sessions occurred.
     * @param today Reference date for current streak calculation.
     * @return [StreakResult] containing current and all-time best consecutive day counts.
     */
    fun calculateStreak(sessionDates: Set<LocalDate>, today: LocalDate): StreakResult {
        if (sessionDates.isEmpty()) {
            return StreakResult(0, 0)
        }

        // Current streak: counts consecutive days back from today (or yesterday if today has no session yet).
        var currentStreak = 0
        var checkDate = if (sessionDates.contains(today)) {
            today
        } else if (sessionDates.contains(today.minusDays(1))) {
            today.minusDays(1)
        } else {
            null
        }

        while (checkDate != null && sessionDates.contains(checkDate)) {
            currentStreak++
            checkDate = checkDate.minusDays(1)
        }

        // Best streak: finds the longest consecutive sequence across all recorded dates.
        val sortedDates = sessionDates.sorted()
        var maxStreak = 0
        var tempStreak = 0
        var previousDate: LocalDate? = null

        for (date in sortedDates) {
            if (previousDate == null || date == previousDate.plusDays(1)) {
                tempStreak++
            } else {
                tempStreak = 1
            }
            if (tempStreak > maxStreak) {
                maxStreak = tempStreak
            }
            previousDate = date
        }

        return StreakResult(
            currentStreak = currentStreak,
            bestStreak = maxStreak
        )
    }
}
