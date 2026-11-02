package com.example.edutrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.example.edutrack.ui.components.GoalReachedDialog
import com.example.edutrack.ui.components.MascotMood
import com.example.edutrack.ui.components.ProfileBottomSheet
import kotlinx.coroutines.launch
import com.example.edutrack.ui.screens.AddSessionScreen
import com.example.edutrack.ui.screens.HistoryScreen
import com.example.edutrack.ui.screens.HomeScreen
import com.example.edutrack.ui.screens.SessionDetailScreen
import com.example.edutrack.ui.screens.StatsScreen
import com.example.edutrack.ui.screens.SubjectManagerScreen
import com.example.edutrack.ui.screens.WelcomeScreen
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
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            val snackbarHostState = remember { SnackbarHostState() }

            val themeViewModel: ThemeViewModel = viewModel(
                factory = ThemeViewModel.provideFactory(appContainer.userPreferencesRepository)
            )
            val themeMode by themeViewModel.themeMode.collectAsState()
            val userName by themeViewModel.userName.collectAsState()
            val avatarPath by themeViewModel.avatarPath.collectAsState()

            var showAppearanceSheet by remember { mutableStateOf(false) }
            var showProfileSheet by remember { mutableStateOf(false) }
            var celebratedSubjectName by remember { mutableStateOf<String?>(null) }

            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            DisposableEffect(darkTheme) {
                (context as? ComponentActivity)?.enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT
                    ) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT
                    ) { darkTheme }
                )
                onDispose {}
            }

            EduTrackTheme(darkTheme = darkTheme) {
                if (userName.isBlank()) {
                    WelcomeScreen(
                        onSaveName = themeViewModel::setUserName
                    )
                } else {
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
                        contentWindowInsets = WindowInsets(0, 0, 0, 0),
                        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                        ) {
                            composable(Screen.Home.route) {
                                HomeScreen(
                                    uiState = homeUiState,
                                    userName = userName,
                                    avatarPath = avatarPath,
                                    onNavigateToSubjects = {
                                        navController.navigate(Screen.Subjects.route)
                                    },
                                    onSessionClick = { sessionId ->
                                        navController.navigate(Screen.SessionDetail.createRoute(sessionId))
                                    },
                                    onOpenAppearanceSheet = {
                                        showAppearanceSheet = true
                                    },
                                    onOpenProfileSheet = {
                                        showProfileSheet = true
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
                                    sessionViewModel.saveSession { isGoalReached, subjectName ->
                                        onSuccess()
                                        if (isGoalReached) {
                                            celebratedSubjectName = subjectName
                                        }
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

                    if (showProfileSheet) {
                        ProfileBottomSheet(
                            currentName = userName,
                            avatarPath = avatarPath,
                            onSaveName = { newName ->
                                themeViewModel.setUserName(newName)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Profile updated")
                                }
                            },
                            onSaveAvatarPath = { newPath ->
                                themeViewModel.setAvatarPath(newPath)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Profile updated")
                                }
                            },
                            onRemovePhoto = {
                                themeViewModel.removeAvatarPhoto(context)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Profile updated")
                                }
                            },
                            onDismissRequest = {
                                showProfileSheet = false
                            }
                        )
                    }

                    celebratedSubjectName?.let { subjectName ->
                        GoalReachedDialog(
                            subjectName = subjectName,
                            onDismissRequest = {
                                celebratedSubjectName = null
                            }
                        )
                    }
                }
            }
        }
    }
}
}
