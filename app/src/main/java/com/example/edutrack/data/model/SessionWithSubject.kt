package com.example.edutrack.data.model

import com.example.edutrack.data.local.StudySession
import com.example.edutrack.data.local.Subject

data class SessionWithSubject(
    val session: StudySession,
    val subject: Subject?
)
