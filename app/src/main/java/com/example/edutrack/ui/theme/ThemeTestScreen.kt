package com.example.edutrack.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ThemeTestScreen(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "EduTrack Theme Test",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Color Palette",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Color items
            ColorItem(name = "Background", color = MaterialTheme.colorScheme.background, hex = if (MaterialTheme.colorScheme.background == DarkBackground) "#121218" else "#F4F5F9")
            ColorItem(name = "Surface / Card", color = MaterialTheme.colorScheme.surface, hex = if (MaterialTheme.colorScheme.surface == DarkSurface) "#1C1C24" else "#FFFFFF")
            ColorItem(name = "Primary", color = MaterialTheme.colorScheme.primary, hex = if (MaterialTheme.colorScheme.primary == DarkPrimary) "#6C5CE7" else "#5C45EE")
            ColorItem(name = "Accent Lime", color = MaterialTheme.colorScheme.secondary, hex = "#D4FF00")
            ColorItem(name = "Text", color = MaterialTheme.colorScheme.onBackground, hex = if (MaterialTheme.colorScheme.onBackground == DarkText) "#FFFFFF" else "#1A1A24")
            ColorItem(name = "Muted Text", color = MaterialTheme.colorScheme.onSurfaceVariant, hex = if (MaterialTheme.colorScheme.onSurfaceVariant == DarkMutedText) "#9E9EA8" else "#666670")

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sample Components",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Sample Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Sample Card (16dp radius)",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Review your focused marathon logs and study blocks.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Sample Pill Tag
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "PHYSICS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.secondary
                ) {
                    Text(
                        text = "ACTIVE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Sample Button
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Save Session",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
fun ColorItem(name: String, color: Color, hex: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.small)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color, MaterialTheme.shapes.extraSmall)
                    .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f), MaterialTheme.shapes.extraSmall)
            )
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = hex,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
fun ThemeTestScreenLightPreview() {
    EduTrackTheme(darkTheme = false) {
        ThemeTestScreen()
    }
}

@Preview(name = "Dark Mode", showBackground = true)
@Composable
fun ThemeTestScreenDarkPreview() {
    EduTrackTheme(darkTheme = true) {
        ThemeTestScreen()
    }
}
