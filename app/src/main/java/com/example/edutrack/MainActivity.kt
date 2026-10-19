package com.example.edutrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.edutrack.ui.theme.EduTrackTheme
import com.example.edutrack.ui.theme.ThemeTestScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EduTrackTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ThemeTestScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
fun MainActivityLightPreview() {
    EduTrackTheme(darkTheme = false) {
        ThemeTestScreen()
    }
}

@Preview(name = "Dark Mode", showBackground = true)
@Composable
fun MainActivityDarkPreview() {
    EduTrackTheme(darkTheme = true) {
        ThemeTestScreen()
    }
}
