package com.example.edutrack.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.edutrack.ui.navigation.Screen

@Composable
fun AppBottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onLogSessionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavBarItem(
                label = "Home",
                icon = Icons.Default.Home,
                selected = currentRoute == Screen.Home.route,
                onClick = { onNavigate(Screen.Home.route) }
            )
            NavBarItem(
                label = "History",
                icon = Icons.Default.CalendarMonth,
                selected = currentRoute == Screen.History.route,
                onClick = { onNavigate(Screen.History.route) }
            )

            // Center Floating Action Button (+)
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary)
                    .clickable { onLogSessionClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Log Session",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }

            NavBarItem(
                label = "Subjects",
                icon = Icons.Default.School,
                selected = currentRoute == Screen.SubjectManager.route,
                onClick = { onNavigate(Screen.SubjectManager.route) }
            )
            NavBarItem(
                label = "Stats",
                icon = Icons.Default.BarChart,
                selected = currentRoute == Screen.Stats.route,
                onClick = { onNavigate(Screen.Stats.route) }
            )
        }
    }
}

@Composable
private fun NavBarItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val activeColor = MaterialTheme.colorScheme.secondary
    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    val color = if (selected) activeColor else inactiveColor

    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}
