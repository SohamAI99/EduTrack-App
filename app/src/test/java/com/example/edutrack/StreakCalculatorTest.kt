package com.example.edutrack

import com.example.edutrack.util.StreakCalculator
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class StreakCalculatorTest {

    private val today = LocalDate.of(2026, 10, 25)

    @Test
    fun calculateStreak_noSessions_returnsZeroAndZero() {
        val result = StreakCalculator.calculateStreak(emptySet(), today)
        assertEquals(0, result.currentStreak)
        assertEquals(0, result.bestStreak)
    }

    @Test
    fun calculateStreak_oneSessionToday_returnsOneAndOne() {
        val sessionDates = setOf(today)
        val result = StreakCalculator.calculateStreak(sessionDates, today)
        assertEquals(1, result.currentStreak)
        assertEquals(1, result.bestStreak)
    }

    @Test
    fun calculateStreak_runOfThreeDaysEndingToday_returnsThreeAndThree() {
        val sessionDates = setOf(
            today.minusDays(2),
            today.minusDays(1),
            today
        )
        val result = StreakCalculator.calculateStreak(sessionDates, today)
        assertEquals(3, result.currentStreak)
        assertEquals(3, result.bestStreak)
    }

    @Test
    fun calculateStreak_runEndingYesterday_todayMissed_streakStillAlive() {
        val sessionDates = setOf(
            today.minusDays(3),
            today.minusDays(2),
            today.minusDays(1)
        )
        val result = StreakCalculator.calculateStreak(sessionDates, today)
        assertEquals(3, result.currentStreak)
        assertEquals(3, result.bestStreak)
    }

    @Test
    fun calculateStreak_missedDay_gapBreaksStreak_currentStreakResetsToZero() {
        val sessionDates = setOf(
            today.minusDays(4),
            today.minusDays(3),
            today.minusDays(2)
        )
        val result = StreakCalculator.calculateStreak(sessionDates, today)
        assertEquals(0, result.currentStreak)
        assertEquals(3, result.bestStreak)
    }

    @Test
    fun calculateStreak_bestStreakLargerThanCurrentStreak() {
        val sessionDates = setOf(
            today.minusDays(10),
            today.minusDays(9),
            today.minusDays(8),
            today.minusDays(7),
            today.minusDays(6),
            today.minusDays(1),
            today
        )
        val result = StreakCalculator.calculateStreak(sessionDates, today)
        assertEquals(2, result.currentStreak)
        assertEquals(5, result.bestStreak)
    }

    @Test
    fun calculateStreak_multipleSessionsOnSameDay_countsOnce() {
        val sessionDates = setOf(today)
        val result = StreakCalculator.calculateStreak(sessionDates, today)
        assertEquals(1, result.currentStreak)
        assertEquals(1, result.bestStreak)
    }
}
