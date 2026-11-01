package com.example.edutrack.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.edutrack.ui.theme.EduTrackTheme

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isAccent: Boolean = false,
    enabled: Boolean = true
) {
    val backgroundColor = if (isAccent) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
    val contentColor = if (isAccent) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary
    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        enabled = enabled,
        shape = MaterialTheme.shapes.extraLarge,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor,
            disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f),
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardShape = RoundedCornerShape(20.dp)
    val cardBorder = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
    val cardColors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    val cardElevation = CardDefaults.cardElevation(defaultElevation = 2.dp)

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = cardShape,
            border = cardBorder,
            colors = cardColors,
            elevation = cardElevation,
            content = content
        )
    } else {
        Card(
            modifier = modifier,
            shape = cardShape,
            border = cardBorder,
            colors = cardColors,
            elevation = cardElevation,
            content = content
        )
    }
}

@Preview(name = "AppButton Light", showBackground = true)
@Composable
fun AppButtonLightPreview() {
    EduTrackTheme(darkTheme = false) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            AppButton(text = "Primary Button", onClick = {})
            AppButton(text = "Accent Button", onClick = {}, isAccent = true)
            AppButton(text = "Disabled Button", onClick = {}, enabled = false)
        }
    }
}

@Preview(name = "AppButton Dark", showBackground = true)
@Composable
fun AppButtonDarkPreview() {
    EduTrackTheme(darkTheme = true) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            AppButton(text = "Primary Button", onClick = {})
            AppButton(text = "Accent Button", onClick = {}, isAccent = true)
            AppButton(text = "Disabled Button", onClick = {}, enabled = false)
        }
    }
}

@Preview(name = "AppCard Light", showBackground = true)
@Composable
fun AppCardLightPreview() {
    EduTrackTheme(darkTheme = false) {
        AppCard(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Card Title", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "This is a reusable AppCard with 20dp rounded corners.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Preview(name = "AppCard Dark", showBackground = true)
@Composable
fun AppCardDarkPreview() {
    EduTrackTheme(darkTheme = true) {
        AppCard(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Card Title", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "This is a reusable AppCard with 20dp rounded corners.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
