package com.example.edutrack.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object History : Screen("history")
    object AddSession : Screen("add_session")
    object Subjects : Screen("subjects")
    object Stats : Screen("stats")
}
