package com.example.edutrack.data.model

import com.example.edutrack.data.local.Subject

data class SubjectProgress(
    val subject: Subject,
    val loggedMinutesThisWeek: Int,
    val goalMinutesThisWeek: Int,
    val progressFraction: Float, // 0.0f to 1.0f (or > 1.0f if exceeded)
    val remainingHoursString: String
)
