package com.example.edutrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.example.edutrack.data.local.Subject
import com.example.edutrack.ui.components.AppButton
import com.example.edutrack.ui.components.AppCard
import com.example.edutrack.ui.theme.EduTrackTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSessionScreen(
    subjects: List<Subject>,
    onBackClick: () -> Unit,
    onSaveSession: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedSubject by remember(subjects) { mutableStateOf(subjects.firstOrNull()) }
    var expandedSubjectDropdown by remember { mutableStateOf(false) }
    var durationMinutesText by remember { mutableStateOf("45") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var notesText by remember { mutableStateOf("") }

    val quickChips = listOf(15, 30, 45, 60)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Log Session",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Course / Subject Section
            item {
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "COURSE / SUBJECT",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        ExposedDropdownMenuBox(
                            expanded = expandedSubjectDropdown,
                            onExpandedChange = { expandedSubjectDropdown = !expandedSubjectDropdown },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedSubject?.name ?: "Select a subject",
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                                leadingIcon = {
                                    selectedSubject?.let { subject ->
                                        val color = try {
                                            Color(subject.colorHex.toColorInt())
                                        } catch (_: Exception) {
                                            MaterialTheme.colorScheme.primary
                                        }
                                        Box(
                                            modifier = Modifier
                                                .size(14.dp)
                                                .background(color, CircleShape)
                                        )
                                    }
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSubjectDropdown)
                                },
                                shape = MaterialTheme.shapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = expandedSubjectDropdown,
                                onDismissRequest = { expandedSubjectDropdown = false }
                            ) {
                                if (subjects.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { Text("No subjects available", style = MaterialTheme.typography.bodyMedium) },
                                        onClick = { expandedSubjectDropdown = false }
                                    )
                                } else {
                                    subjects.forEach { subject ->
                                        val color = try {
                                            Color(subject.colorHex.toColorInt())
                                        } catch (_: Exception) {
                                            MaterialTheme.colorScheme.primary
                                        }
                                        DropdownMenuItem(
                                            text = {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(12.dp)
                                                            .background(color, CircleShape)
                                                    )
                                                    Text(
                                                        text = subject.name,
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                }
                                            },
                                            onClick = {
                                                selectedSubject = subject
                                                expandedSubjectDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Duration Section
            item {
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "DURATION (MINUTES)",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick chips row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            quickChips.forEach { mins ->
                                FilterChip(
                                    selected = durationMinutesText == mins.toString(),
                                    onClick = { durationMinutesText = mins.toString() },
                                    label = {
                                        Text(
                                            text = "$mins min",
                                            style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp)
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = durationMinutesText,
                            onValueChange = { input ->
                                if (input.isEmpty() || input.all { it.isDigit() }) {
                                    durationMinutesText = input
                                }
                            },
                            label = { Text("Duration in minutes", style = MaterialTheme.typography.bodyMedium) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = MaterialTheme.shapes.medium,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                            )
                        )
                    }
                }
            }

            // Date & Time Section
            item {
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "DATE & TIME",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Date",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = formatDateDisplay(selectedDate),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                OutlinedButton(
                                    onClick = { showDatePickerDialog = true },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    shape = MaterialTheme.shapes.small
                                ) {
                                    Text(
                                        text = "Change",
                                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Reflection & Notes Section
            item {
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "SESSION REFLECTION & NOTES",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = notesText,
                            onValueChange = { notesText = it },
                            placeholder = {
                                Text(
                                    text = "Reviewed lecture slides and attempted practice problems...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 100.dp),
                            minLines = 3,
                            maxLines = 5,
                            shape = MaterialTheme.shapes.medium,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                            )
                        )
                    }
                }
            }

            // Action Buttons
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Text("Cancel", style = MaterialTheme.typography.labelLarge)
                    }

                    AppButton(
                        text = "Save Session",
                        onClick = onSaveSession,
                        isAccent = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        if (showDatePickerDialog) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            )
            DatePickerDialog(
                onDismissRequest = { showDatePickerDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                selectedDate = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            }
                            showDatePickerDialog = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePickerDialog = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

private fun formatDateDisplay(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
    val formatted = date.format(formatter)
    return if (date == LocalDate.now()) {
        "Today, $formatted"
    } else {
        formatted
    }
}

@Preview(name = "Add Session Light", showBackground = true)
@Composable
fun AddSessionLightPreview() {
    EduTrackTheme(darkTheme = false) {
        AddSessionScreen(
            subjects = listOf(
                Subject(id = 1, name = "Physics", colorHex = "#A3E635", weeklyGoalHours = 4, createdAt = LocalDate.now()),
                Subject(id = 2, name = "Mathematics", colorHex = "#6C5CE7", weeklyGoalHours = 5, createdAt = LocalDate.now())
            ),
            onBackClick = {}
        )
    }
}

@Preview(name = "Add Session Dark", showBackground = true)
@Composable
fun AddSessionDarkPreview() {
    EduTrackTheme(darkTheme = true) {
        AddSessionScreen(
            subjects = listOf(
                Subject(id = 1, name = "Physics", colorHex = "#A3E635", weeklyGoalHours = 4, createdAt = LocalDate.now()),
                Subject(id = 2, name = "Mathematics", colorHex = "#6C5CE7", weeklyGoalHours = 5, createdAt = LocalDate.now())
            ),
            onBackClick = {}
        )
    }
}
