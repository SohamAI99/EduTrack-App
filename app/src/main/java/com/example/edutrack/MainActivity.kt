package com.example.edutrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.edutrack.model.ThemeMode
import com.example.edutrack.navigation.Screen
import com.example.edutrack.ui.components.AppBottomBar
import com.example.edutrack.ui.components.AppearanceBottomSheet
import com.example.edutrack.ui.components.MascotMood
import com.example.edutrack.ui.screens.AddSessionScreen
import com.example.edutrack.ui.screens.HistoryScreen
import com.example.edutrack.ui.screens.HomeScreen
import com.example.edutrack.ui.screens.SessionDetailScreen
import com.example.edutrack.ui.screens.StatsScreen
import com.example.edutrack.ui.screens.SubjectManagerScreen
import com.example.edutrack.ui.theme.EduTrackTheme
import com.example.edutrack.ui.viewmodel.HistoryViewModel
import com.example.edutrack.ui.viewmodel.HomeViewModel
import com.example.edutrack.ui.viewmodel.SessionDetailViewModel
import com.example.edutrack.ui.viewmodel.SessionViewModel
import com.example.edutrack.ui.viewmodel.StatsViewModel
import com.example.edutrack.ui.viewmodel.SubjectViewModel
import com.example.edutrack.ui.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appContainer = (application as EduTrackApplication).container
        setContent {
            val themeViewModel: ThemeViewModel = viewModel(
                factory = ThemeViewModel.provideFactory(appContainer.userPreferencesRepository)
            )
            val themeMode by themeViewModel.themeMode.collectAsState()
            var showAppearanceSheet by remember { mutableStateOf(false) }

            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            EduTrackTheme(darkTheme = darkTheme) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val subjectViewModel: SubjectViewModel = viewModel(
                    factory = SubjectViewModel.provideFactory(appContainer.repository)
                )
                val sessionViewModel: SessionViewModel = viewModel(
                    factory = SessionViewModel.provideFactory(appContainer.repository)
                )
                val historyViewModel: HistoryViewModel = viewModel(
                    factory = HistoryViewModel.provideFactory(appContainer.repository)
                )
                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModel.provideFactory(appContainer.repository)
                )
                val statsViewModel: StatsViewModel = viewModel(
                    factory = StatsViewModel.provideFactory(appContainer.repository)
                )

                val subjects by subjectViewModel.subjects.collectAsState()
                val formState by subjectViewModel.formState.collectAsState()
                val sessionFormState by sessionViewModel.formState.collectAsState()
                val historyUiState by historyViewModel.uiState.collectAsState()
                val homeUiState by homeViewModel.uiState.collectAsState()
                val statsUiState by statsViewModel.uiState.collectAsState()

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
                            HomeScreen(
                                uiState = homeUiState,
                                onNavigateToSubjects = {
                                    navController.navigate(Screen.Subjects.route)
                                },
                                onSessionClick = { sessionId ->
                                    navController.navigate(Screen.SessionDetail.createRoute(sessionId))
                                },
                                onOpenAppearanceSheet = {
                                    showAppearanceSheet = true
                                }
                            )
                        }
                        composable(Screen.History.route) {
                            HistoryScreen(
                                sessions = historyUiState.filteredSessions,
                                hasAnySessions = historyUiState.hasAnySessions,
                                subjects = historyUiState.subjects,
                                selectedSubjectId = historyUiState.selectedSubjectId,
                                dateRange = historyUiState.dateRange,
                                isFilterActive = historyUiState.isFilterActive,
                                onSubjectSelect = historyViewModel::selectSubjectFilter,
                                onDateRangeSelect = historyViewModel::setDateRangeFilter,
                                onClearFilters = historyViewModel::clearFilters,
                                onSessionClick = { sessionId ->
                                    navController.navigate(Screen.SessionDetail.createRoute(sessionId))
                                }
                            )
                        }
                        composable(
                            route = Screen.SessionDetail.route,
                            arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: 0L
                            val detailViewModel: SessionDetailViewModel = viewModel(
                                factory = SessionDetailViewModel.provideFactory(sessionId, appContainer.repository),
                                key = "SessionDetail_$sessionId"
                            )
                            val sessionDetail by detailViewModel.uiState.collectAsState()

                            SessionDetailScreen(
                                session = sessionDetail,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onEditClick = {
                                    navController.navigate(Screen.AddSession.createRoute(sessionId))
                                },
                                onDeleteClick = {
                                    detailViewModel.deleteSession {
                                        navController.popBackStack()
                                    }
                                }
                            )
                        }
                        composable(
                            route = Screen.AddSession.route,
                            arguments = listOf(navArgument("sessionId") { type = NavType.LongType; defaultValue = -1L })
                        ) { backStackEntry ->
                            val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: -1L
                            LaunchedEffect(sessionId) {
                                sessionViewModel.loadSessionForEditing(sessionId)
                            }

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
                                    sessionViewModel.saveSession {
                                        onSuccess()
                                        if (sessionId != -1L) {
                                            navController.popBackStack()
                                        }
                                    }
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
                            StatsScreen(
                                uiState = statsUiState
                            )
                        }
                    }

                    if (showAppearanceSheet) {
                        AppearanceBottomSheet(
                            selectedThemeMode = themeMode,
                            onThemeModeSelected = themeViewModel::setThemeMode,
                            onDismissRequest = {
                                showAppearanceSheet = false
                            }
                        )
                    }
                }
            }
        }
    }
}
