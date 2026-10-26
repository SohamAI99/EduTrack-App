package com.example.edutrack.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edutrack.navigation.Screen

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

val BOTTOM_NAV_ITEMS = listOf(
    BottomNavItem(Screen.Home.route, "Home", Icons.Default.Home),
    BottomNavItem(Screen.History.route, "History", Icons.Default.History),
    BottomNavItem(Screen.Subjects.route, "Subjects", Icons.Default.Category),
    BottomNavItem(Screen.Stats.route, "Stats", Icons.AutoMirrored.Filled.ShowChart)
)

@Composable
fun AppBottomBar(
    currentRoute: String?,
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (currentRoute == Screen.AddSession.route) {
        return
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            NavItemCell(
                item = BOTTOM_NAV_ITEMS[0],
                isSelected = currentRoute == BOTTOM_NAV_ITEMS[0].route,
                onClick = { onNavigateToRoute(BOTTOM_NAV_ITEMS[0].route) }
            )

            NavItemCell(
                item = BOTTOM_NAV_ITEMS[1],
                isSelected = currentRoute == BOTTOM_NAV_ITEMS[1].route,
                onClick = { onNavigateToRoute(BOTTOM_NAV_ITEMS[1].route) }
            )

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary)
                    .clickable { onNavigateToRoute(Screen.AddSession.route) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Session",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }

            NavItemCell(
                item = BOTTOM_NAV_ITEMS[2],
                isSelected = currentRoute == BOTTOM_NAV_ITEMS[2].route,
                onClick = { onNavigateToRoute(BOTTOM_NAV_ITEMS[2].route) }
            )

            NavItemCell(
                item = BOTTOM_NAV_ITEMS[3],
                isSelected = currentRoute == BOTTOM_NAV_ITEMS[3].route,
                onClick = { onNavigateToRoute(BOTTOM_NAV_ITEMS[3].route) }
            )
        }
    }
}

@Composable
private fun NavItemCell(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            ),
            color = contentColor
        )
    }
}
