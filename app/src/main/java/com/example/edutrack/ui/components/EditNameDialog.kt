package com.example.edutrack.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EditNameDialog(
    currentName: String,
    onSaveName: (String) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    var nameText by remember { mutableStateOf(currentName) }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = "Edit Your Name",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = nameText,
                    onValueChange = { input ->
                        if (input.length <= 20) {
                            nameText = input
                            if (showError && input.trim().isNotEmpty()) {
                                showError = false
                            }
                        }
                    },
                    label = { Text("Name or Nickname") },
                    isError = showError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (showError) {
                    Text(
                        text = "Name cannot be empty",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val trimmed = nameText.trim()
                    if (trimmed.isEmpty()) {
                        showError = true
                    } else {
                        onSaveName(trimmed)
                        onDismissRequest()
                    }
                }
            ) {
                Text("Save", style = MaterialTheme.typography.labelLarge)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel", style = MaterialTheme.typography.labelLarge)
            }
        },
        modifier = modifier
    )
}
