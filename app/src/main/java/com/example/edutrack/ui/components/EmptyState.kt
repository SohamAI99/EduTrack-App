package com.example.edutrack.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.edutrack.ui.theme.EduTrackTheme

@Composable
fun EmptyState(
    mood: MascotMood,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null,
    isButtonAccent: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = mood.drawableRes),
            contentDescription = title,
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (buttonText != null && onButtonClick != null) {
            Spacer(modifier = Modifier.height(24.dp))
            AppButton(
                text = buttonText,
                onClick = onButtonClick,
                isAccent = isButtonAccent,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        }
    }
}

@Preview(name = "EmptyState Happy Light", showBackground = true)
@Composable
fun EmptyStateHappyLightPreview() {
    EduTrackTheme(darkTheme = false) {
        EmptyState(
            mood = MascotMood.HAPPY,
            title = "All Caught Up",
            message = "You have no pending study sessions right now.",
            buttonText = "Start Session",
            onButtonClick = {}
        )
    }
}

@Preview(name = "EmptyState Happy Dark", showBackground = true)
@Composable
fun EmptyStateHappyDarkPreview() {
    EduTrackTheme(darkTheme = true) {
        EmptyState(
            mood = MascotMood.HAPPY,
            title = "All Caught Up",
            message = "You have no pending study sessions right now.",
            buttonText = "Start Session",
            onButtonClick = {}
        )
    }
}

@Preview(name = "EmptyState Celebrate Light", showBackground = true)
@Composable
fun EmptyStateCelebrateLightPreview() {
    EduTrackTheme(darkTheme = false) {
        EmptyState(
            mood = MascotMood.CELEBRATE,
            title = "Goal Reached!",
            message = "Amazing job completing your study streak today.",
            buttonText = "View Stats",
            onButtonClick = {},
            isButtonAccent = true
        )
    }
}

@Preview(name = "EmptyState Celebrate Dark", showBackground = true)
@Composable
fun EmptyStateCelebrateDarkPreview() {
    EduTrackTheme(darkTheme = true) {
        EmptyState(
            mood = MascotMood.CELEBRATE,
            title = "Goal Reached!",
            message = "Amazing job completing your study streak today.",
            buttonText = "View Stats",
            onButtonClick = {},
            isButtonAccent = true
        )
    }
}

@Preview(name = "EmptyState Sleepy Light", showBackground = true)
@Composable
fun EmptyStateSleepyLightPreview() {
    EduTrackTheme(darkTheme = false) {
        EmptyState(
            mood = MascotMood.SLEEPY,
            title = "No Data Yet",
            message = "Log your first study session to see analytics.",
            buttonText = "Add Subject",
            onButtonClick = {}
        )
    }
}

@Preview(name = "EmptyState Sleepy Dark", showBackground = true)
@Composable
fun EmptyStateSleepyDarkPreview() {
    EduTrackTheme(darkTheme = true) {
        EmptyState(
            mood = MascotMood.SLEEPY,
            title = "No Data Yet",
            message = "Log your first study session to see analytics.",
            buttonText = "Add Subject",
            onButtonClick = {}
        )
    }
}

@Preview(name = "EmptyState Study Light", showBackground = true)
@Composable
fun EmptyStateStudyLightPreview() {
    EduTrackTheme(darkTheme = false) {
        EmptyState(
            mood = MascotMood.STUDY,
            title = "No Sessions Logged",
            message = "Get started by creating a study schedule.",
            buttonText = "Log Session",
            onButtonClick = {}
        )
    }
}

@Preview(name = "EmptyState Study Dark", showBackground = true)
@Composable
fun EmptyStateStudyDarkPreview() {
    EduTrackTheme(darkTheme = true) {
        EmptyState(
            mood = MascotMood.STUDY,
            title = "No Sessions Logged",
            message = "Get started by creating a study schedule.",
            buttonText = "Log Session",
            onButtonClick = {}
        )
    }
}
