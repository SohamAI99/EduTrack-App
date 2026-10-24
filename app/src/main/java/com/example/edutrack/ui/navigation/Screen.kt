package com.example.edutrack.ui.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Welcome : Screen("welcome")
    object Home : Screen("home")
    object History : Screen("history")
    object LogSession : Screen("log_session?sessionId={sessionId}") {
        fun createRoute(sessionId: Long? = null): String {
            return if (sessionId != null) "log_session?sessionId=$sessionId" else "log_session"
        }
    }
    object SessionDetail : Screen("session_detail/{sessionId}") {
        fun createRoute(sessionId: Long): String {
            return "session_detail/$sessionId"
        }
    }
    object SubjectManager : Screen("subject_manager")
    object Stats : Screen("stats")
}
