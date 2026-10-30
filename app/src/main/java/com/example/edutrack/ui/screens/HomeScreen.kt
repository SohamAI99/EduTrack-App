package com.example.edutrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edutrack.ui.components.AppButton
import com.example.edutrack.ui.components.EmptyState
import com.example.edutrack.ui.components.MascotMood
import com.example.edutrack.ui.theme.EduTrackTheme
import com.example.edutrack.ui.viewmodel.HomeUiState
import com.example.edutrack.ui.viewmodel.formatDurationCompact

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onNavigateToSubjects: () -> Unit = {},
    onSessionClick: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Home",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (!uiState.hasSubjects) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    EmptyState(
                        mood = MascotMood.HAPPY,
                        title = "Add your first subject",
                        message = "Create a subject to start setting weekly study goals."
                    )
                    AppButton(
                        text = "Go to Subjects",
                        onClick = onNavigateToSubjects,
                        isAccent = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Greeting Header
                item {
                    Column {
                        Text(
                            text = "Hey there",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Ready to level up tonight?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Summary Card with Today's Total
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "TODAY",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = formatDurationCompact(uiState.todayTotalMinutes),
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Home Screen Light", showBackground = true)
@Composable
fun HomeScreenLightPreview() {
    EduTrackTheme(darkTheme = false) {
        HomeScreen(
            uiState = HomeUiState(
                hasSubjects = true,
                todayTotalMinutes = 105,
                weeklyTotalMinutes = 300,
                weeklyGoalMinutes = 600
            )
        )
    }
}

@Preview(name = "Home Screen Dark", showBackground = true)
@Composable
fun HomeScreenDarkPreview() {
    EduTrackTheme(darkTheme = true) {
        HomeScreen(
            uiState = HomeUiState(
                hasSubjects = true,
                todayTotalMinutes = 105,
                weeklyTotalMinutes = 300,
                weeklyGoalMinutes = 600
            )
        )
    }
}
