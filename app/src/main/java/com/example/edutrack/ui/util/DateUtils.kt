package com.example.edutrack.ui.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {
    private val formatterWithYear = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault())
    private val fullDateFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.getDefault())

    fun formatDateDisplay(date: LocalDate): String {
        val today = LocalDate.now()
        val formatted = date.format(formatterWithYear)
        return when {
            date.isEqual(today) -> "Today, $formatted"
            date.isEqual(today.minusDays(1)) -> "Yesterday, $formatted"
            else -> formatted
        }
    }

    fun formatDuration(minutes: Int): String {
        val hours = minutes / 60
        val remainingMins = minutes % 60
        return when {
            hours > 0 && remainingMins > 0 -> "${hours}h ${remainingMins}m"
            hours > 0 -> "${hours}h 00m"
            else -> "${minutes} min"
        }
    }

    fun formatDurationShort(minutes: Int): String {
        val hours = minutes / 60
        val remainingMins = minutes % 60
        return when {
            hours > 0 && remainingMins > 0 -> "${hours}h ${remainingMins}m"
            hours > 0 -> "${hours}h"
            else -> "${minutes}m"
        }
    }

    fun formatHoursDecimal(minutes: Int): String {
        val hours = minutes / 60.0
        return if (hours % 1.0 == 0.0) {
            "${hours.toInt()}h"
        } else {
            String.format(Locale.getDefault(), "%.1fh", hours)
        }
    }
}
