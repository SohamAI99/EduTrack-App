package com.example.edutrack.data.local

import com.example.edutrack.data.repository.Repository
import kotlinx.coroutines.flow.first
import java.time.LocalDate

object SeedData {
    suspend fun populateIfEmpty(repository: Repository) {
        val existingSubjects = repository.getAllSubjects().first()
        if (existingSubjects.isNotEmpty()) return

        val today = LocalDate.now()

        // 1. Calculus & Linear Algebra
        val subject1Id = repository.insertSubject(
            Subject(
                name = "Calculus & Linear Algebra",
                colorHex = "#6C5CE7",
                weeklyGoalHours = 6,
                createdAt = today.minusDays(14)
            )
        )

        // 2. Quantum Mechanics
        val subject2Id = repository.insertSubject(
            Subject(
                name = "Quantum Mechanics",
                colorHex = "#00B894",
                weeklyGoalHours = 5,
                createdAt = today.minusDays(14)
            )
        )

        // 3. Modern Essay Analysis
        val subject3Id = repository.insertSubject(
            Subject(
                name = "Modern Essay Analysis",
                colorHex = "#E17055",
                weeklyGoalHours = 4,
                createdAt = today.minusDays(14)
            )
        )

        // 4. Distributed Systems & Go
        val subject4Id = repository.insertSubject(
            Subject(
                name = "Distributed Systems & Go",
                colorHex = "#00CEC9",
                weeklyGoalHours = 12,
                createdAt = today.minusDays(14)
            )
        )

        // Seed Sessions
        // Today
        repository.insertSession(
            StudySession(
                subjectId = subject2Id,
                durationMinutes = 45,
                date = today,
                notes = "Reviewed lecture slides on rotational dynamics and attempted practice problems #1-8. Solid grasp on torque equations, need review on angular momentum vectors.",
                tag = "Practice Problems",
                createdAt = today
            )
        )
        repository.insertSession(
            StudySession(
                subjectId = subject1Id,
                durationMinutes = 60,
                date = today,
                notes = "Eigenvalues and matrix diagonalization review with problem set solutions.",
                tag = "Midterm Prep",
                createdAt = today
            )
        )

        // Yesterday
        repository.insertSession(
            StudySession(
                subjectId = subject3Id,
                durationMinutes = 45,
                date = today.minusDays(1),
                notes = "Drafted thesis statement and 2 body paragraphs for analytical paper.",
                tag = "Lecture Review",
                createdAt = today.minusDays(1)
            )
        )
        repository.insertSession(
            StudySession(
                subjectId = subject2Id,
                durationMinutes = 30,
                date = today.minusDays(1),
                notes = "Lab report data plotting and error propagation analysis.",
                tag = "Project Sprint",
                createdAt = today.minusDays(1)
            )
        )

        // 2 Days ago
        repository.insertSession(
            StudySession(
                subjectId = subject1Id,
                durationMinutes = 45,
                date = today.minusDays(2),
                notes = "Calculus integration by parts practice sprint and trigonometric substitution.",
                tag = "Practice Problems",
                createdAt = today.minusDays(2)
            )
        )

        // 3 Days ago
        repository.insertSession(
            StudySession(
                subjectId = subject4Id,
                durationMinutes = 60,
                date = today.minusDays(3),
                notes = "Implemented consensus protocol and worker goroutine pool.",
                tag = "Project Sprint",
                createdAt = today.minusDays(3)
            )
        )

        // 4 Days ago
        repository.insertSession(
            StudySession(
                subjectId = subject4Id,
                durationMinutes = 90,
                date = today.minusDays(4),
                notes = "Network socket programming and high-performance RPC serialization.",
                tag = "Practice Problems",
                createdAt = today.minusDays(4)
            )
        )
    }
}
