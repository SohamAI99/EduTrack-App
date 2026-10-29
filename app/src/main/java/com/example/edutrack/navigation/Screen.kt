package com.example.edutrack.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object History : Screen("history")
    object AddSession : Screen("add_session")
    object Subjects : Screen("subjects")
    object Stats : Screen("stats")
    object SessionDetail : Screen("session_detail/{sessionId}") {
        fun createRoute(sessionId: Long) = "session_detail/$sessionId"
    }
}
