package com.example.edutrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.edutrack.navigation.Screen
import com.example.edutrack.ui.components.AppBottomBar
import com.example.edutrack.ui.components.MascotMood
import com.example.edutrack.ui.screens.AddSessionScreen
import com.example.edutrack.ui.screens.PlaceholderScreen
import com.example.edutrack.ui.screens.SubjectManagerScreen
import com.example.edutrack.ui.theme.EduTrackTheme
import com.example.edutrack.ui.viewmodel.SessionViewModel
import com.example.edutrack.ui.viewmodel.SubjectViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appContainer = (application as EduTrackApplication).container
        setContent {
            EduTrackTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val subjectViewModel: SubjectViewModel = viewModel(
                    factory = SubjectViewModel.provideFactory(appContainer.repository)
                )
                val sessionViewModel: SessionViewModel = viewModel(
                    factory = SessionViewModel.provideFactory(appContainer.repository)
                )

                val subjects by subjectViewModel.subjects.collectAsState()
                val formState by subjectViewModel.formState.collectAsState()
                val sessionFormState by sessionViewModel.formState.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        AppBottomBar(
                            currentRoute = currentRoute,
                            onNavigateToRoute = { route ->
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Home.route) {
                            PlaceholderScreen(
                                title = "Home",
                                mood = MascotMood.HAPPY,
                                message = "Welcome to EduTrack! Your study dashboard is coming soon."
                            )
                        }
                        composable(Screen.History.route) {
                            PlaceholderScreen(
                                title = "History",
                                mood = MascotMood.STUDY,
                                message = "No study history yet. Complete a session to see it logged here!"
                            )
                        }
                        composable(Screen.AddSession.route) {
                            AddSessionScreen(
                                subjects = subjects,
                                formState = sessionFormState,
                                onSubjectChange = sessionViewModel::updateSelectedSubject,
                                onDurationChange = sessionViewModel::updateDurationText,
                                onDateChange = sessionViewModel::updateDate,
                                onNotesChange = sessionViewModel::updateNotesText,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onSaveSession = { onSuccess ->
                                    sessionViewModel.saveSession(onSuccess = onSuccess)
                                },
                                onNavigateToSubjects = {
                                    navController.navigate(Screen.Subjects.route)
                                }
                            )
                        }
                        composable(Screen.Subjects.route) {
                            SubjectManagerScreen(
                                subjects = subjects,
                                formState = formState,
                                onNameChange = subjectViewModel::updateName,
                                onColorSelect = subjectViewModel::updateColor,
                                onIncrementGoal = subjectViewModel::incrementGoalHours,
                                onDecrementGoal = subjectViewModel::decrementGoalHours,
                                onSubmitForm = { onSuccess ->
                                    val isEdit = subjectViewModel.formState.value.editingSubjectId != null
                                    subjectViewModel.saveSubject {
                                        onSuccess(if (isEdit) "Subject updated" else "Subject saved")
                                    }
                                },
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onEditSubject = subjectViewModel::editSubject,
                                onDeleteSubject = { subject, onSuccess ->
                                    subjectViewModel.deleteSubject(subject, onSuccess)
                                },
                                onCancelEdit = subjectViewModel::cancelEdit
                            )
                        }
                        composable(Screen.Stats.route) {
                            PlaceholderScreen(
                                title = "Stats",
                                mood = MascotMood.STUDY,
                                message = "Study analytics and charts will appear here soon."
                            )
                        }
                    }
                }
            }
        }
    }
}
