package com.example.edutrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.example.edutrack.data.local.Subject
import com.example.edutrack.ui.components.AppCard
import com.example.edutrack.ui.components.EmptyState
import com.example.edutrack.ui.components.MascotMood
import com.example.edutrack.ui.theme.EduTrackTheme
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectManagerScreen(
    subjects: List<Subject>,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Subject Manager",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (subjects.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    mood = MascotMood.SLEEPY,
                    title = "No subjects yet",
                    message = "Add your first subject to get started tracking your study goals."
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(subjects) { subject ->
                    SubjectCard(subject = subject)
                }
            }
        }
    }
}

@Composable
fun SubjectCard(subject: Subject) {
    val color = try {
        Color(subject.colorHex.toColorInt())
    } catch (_: Exception) {
        MaterialTheme.colorScheme.primary
    }

    AppCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(color, CircleShape)
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Goal: ${subject.weeklyGoalHours} hrs per week",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(name = "Subject List Light", showBackground = true)
@Composable
fun SubjectListLightPreview() {
    EduTrackTheme(darkTheme = false) {
        SubjectManagerScreen(
            subjects = listOf(
                Subject(id = 1, name = "Computer Science", colorHex = "#6C5CE7", weeklyGoalHours = 6, createdAt = LocalDate.now()),
                Subject(id = 2, name = "Advanced Calculus", colorHex = "#00CEC9", weeklyGoalHours = 4, createdAt = LocalDate.now()),
                Subject(id = 3, name = "Quantum Mechanics", colorHex = "#00B894", weeklyGoalHours = 4, createdAt = LocalDate.now()),
                Subject(id = 4, name = "Modern Literature", colorHex = "#E17055", weeklyGoalHours = 3, createdAt = LocalDate.now())
            ),
            onBackClick = {}
        )
    }
}

@Preview(name = "Subject List Dark", showBackground = true)
@Composable
fun SubjectListDarkPreview() {
    EduTrackTheme(darkTheme = true) {
        SubjectManagerScreen(
            subjects = listOf(
                Subject(id = 1, name = "Computer Science", colorHex = "#6C5CE7", weeklyGoalHours = 6, createdAt = LocalDate.now()),
                Subject(id = 2, name = "Advanced Calculus", colorHex = "#00CEC9", weeklyGoalHours = 4, createdAt = LocalDate.now()),
                Subject(id = 3, name = "Quantum Mechanics", colorHex = "#00B894", weeklyGoalHours = 4, createdAt = LocalDate.now()),
                Subject(id = 4, name = "Modern Literature", colorHex = "#E17055", weeklyGoalHours = 3, createdAt = LocalDate.now())
            ),
            onBackClick = {}
        )
    }
}

@Preview(name = "Subject Empty Light", showBackground = true)
@Composable
fun SubjectEmptyLightPreview() {
    EduTrackTheme(darkTheme = false) {
        SubjectManagerScreen(
            subjects = emptyList(),
            onBackClick = {}
        )
    }
}

@Preview(name = "Subject Empty Dark", showBackground = true)
@Composable
fun SubjectEmptyDarkPreview() {
    EduTrackTheme(darkTheme = true) {
        SubjectManagerScreen(
            subjects = emptyList(),
            onBackClick = {}
        )
    }
}
