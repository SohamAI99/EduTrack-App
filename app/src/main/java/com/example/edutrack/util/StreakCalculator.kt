package com.example.edutrack.util

import java.time.LocalDate

data class StreakResult(
    val currentStreak: Int,
    val bestStreak: Int
)

object StreakCalculator {

    fun calculateStreak(sessionDates: Set<LocalDate>, today: LocalDate): StreakResult {
        if (sessionDates.isEmpty()) {
            return StreakResult(0, 0)
        }

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
