package com.example.edutrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.example.edutrack.data.local.Subject
import com.example.edutrack.ui.components.AppButton
import com.example.edutrack.ui.components.AppCard
import com.example.edutrack.ui.components.EmptyState
import com.example.edutrack.ui.components.MascotMood
import com.example.edutrack.ui.theme.EduTrackTheme
import com.example.edutrack.ui.viewmodel.DateRange
import com.example.edutrack.ui.viewmodel.SessionItemUiModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    sessions: List<SessionItemUiModel>,
    hasAnySessions: Boolean = true,
    subjects: List<Subject> = emptyList(),
    selectedSubjectId: Long? = null,
    dateRange: DateRange? = null,
    isFilterActive: Boolean = false,
    onSubjectSelect: (Long?) -> Unit = {},
    onDateRangeSelect: (LocalDate?, LocalDate?) -> Unit = { _, _ -> },
    onClearFilters: () -> Unit = {},
    onSessionClick: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showDateRangePicker by remember { mutableStateOf(false) }

    val groupedSessions = remember(sessions) {
        sessions.groupBy { it.date }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "History",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "FILTERS",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (isFilterActive) {
                    TextButton(
                        onClick = onClearFilters,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Clear",
                            style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (subjects.isNotEmpty()) {
                SubjectFilterRow(
                    subjects = subjects,
                    selectedSubjectId = selectedSubjectId,
                    onSubjectSelect = onSubjectSelect
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            DateRangeFilterRow(
                dateRange = dateRange,
                onOpenDatePicker = { showDateRangePicker = true }
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (!hasAnySessions) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        mood = MascotMood.STUDY,
                        title = "No sessions yet",
                        message = "Complete a study session to see your logs here."
                    )
                }
            } else if (sessions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        EmptyState(
                            mood = MascotMood.SLEEPY,
                            title = "No sessions match your filters",
                            message = "Try selecting a different subject or adjusting your time window."
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        AppButton(
                            text = "Clear Filters",
                            onClick = onClearFilters,
                            isAccent = true
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    groupedSessions.forEach { (date, daySessions) ->
                        item(key = "header_$date") {
                            val title = formatDateHeaderTitle(date)
                            val sessionCountText = if (daySessions.size == 1) "1 session" else "${daySessions.size} sessions"
                            val totalTimeText = calculateDayTotalTime(daySessions)

                            Spacer(modifier = Modifier.height(8.dp))
                            DateHeaderRow(
                                title = title,
                                sessionCountText = sessionCountText,
                                totalTimeText = totalTimeText
                            )
                        }

                        items(daySessions, key = { it.id }) { session ->
                            SessionCard(
                                session = session,
                                onClick = { onSessionClick(session.id) }
                            )
                        }
                    }
                }
            }
        }

        if (showDateRangePicker) {
            val dateRangePickerState = rememberDateRangePickerState(
                initialSelectedStartDateMillis = dateRange?.startDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
                initialSelectedEndDateMillis = dateRange?.endDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
            )

            DatePickerDialog(
                onDismissRequest = { showDateRangePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val startMillis = dateRangePickerState.selectedStartDateMillis
                            val endMillis = dateRangePickerState.selectedEndDateMillis
                            if (startMillis != null && endMillis != null) {
                                val startDate = Instant.ofEpochMilli(startMillis).atZone(ZoneId.systemDefault()).toLocalDate()
                                val endDate = Instant.ofEpochMilli(endMillis).atZone(ZoneId.systemDefault()).toLocalDate()
                                onDateRangeSelect(startDate, endDate)
                            } else {
                                onDateRangeSelect(null, null)
                            }
                            showDateRangePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDateRangePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DateRangePicker(
                    state = dateRangePickerState,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun DateRangeFilterRow(
    dateRange: DateRange?,
    onOpenDatePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateText = if (dateRange != null) {
        val formatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault())
        "${dateRange.startDate.format(formatter)} - ${dateRange.endDate.format(formatter)}"
    } else {
        "All time"
    }

    AppCard(
        onClick = onOpenDatePicker,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Time Window",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = "TIME WINDOW",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
            ) {
                Text(
                    text = "Select Range",
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun SubjectFilterRow(
    subjects: List<Subject>,
    selectedSubjectId: Long?,
    onSubjectSelect: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        item {
            FilterChip(
                selected = selectedSubjectId == null,
                onClick = { onSubjectSelect(null) },
                label = {
                    Text(
                        text = "All",
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp)
                    )
                },
                shape = MaterialTheme.shapes.extraLarge,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }

        items(subjects, key = { it.id }) { subject ->
            val color = try {
                Color(subject.colorHex.toColorInt())
            } catch (_: Exception) {
                MaterialTheme.colorScheme.primary
            }
            val isSelected = selectedSubjectId == subject.id

            FilterChip(
                selected = isSelected,
                onClick = { onSubjectSelect(if (isSelected) null else subject.id) },
                leadingIcon = {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(color, CircleShape)
                    )
                },
                label = {
                    Text(
                        text = subject.name,
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp)
                    )
                },
                shape = MaterialTheme.shapes.extraLarge,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
fun DateHeaderRow(
    title: String,
    sessionCountText: String,
    totalTimeText: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = sessionCountText,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = totalTimeText,
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

fun formatDateHeaderTitle(date: LocalDate): String {
    val today = LocalDate.now()
    return when (date) {
        today -> "Today"
        today.minusDays(1) -> "Yesterday"
        else -> date.format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault()))
    }
}

fun calculateDayTotalTime(groupSessions: List<SessionItemUiModel>): String {
    val totalMinutes = groupSessions.sumOf { it.durationMinutes }
    val hrs = totalMinutes / 60
    val mins = totalMinutes % 60
    return when {
        hrs > 0 && mins > 0 -> "${hrs}h ${mins}m"
        hrs > 0 -> "${hrs}h 00m"
        else -> "${mins}m"
    }
}

@Preview(name = "History Empty Light", showBackground = true)
@Composable
fun HistoryEmptyLightPreview() {
    EduTrackTheme(darkTheme = false) {
        HistoryScreen(sessions = emptyList())
    }
}

@Preview(name = "History Empty Dark", showBackground = true)
@Composable
fun HistoryEmptyDarkPreview() {
    EduTrackTheme(darkTheme = true) {
        HistoryScreen(sessions = emptyList(), hasAnySessions = false)
    }
}

@Preview(name = "History Filter Empty Light", showBackground = true)
@Composable
fun HistoryFilterEmptyLightPreview() {
    EduTrackTheme(darkTheme = false) {
        HistoryScreen(
            sessions = emptyList(),
            hasAnySessions = true,
            isFilterActive = true
        )
    }
}

@Preview(name = "History Filter Empty Dark", showBackground = true)
@Composable
fun HistoryFilterEmptyDarkPreview() {
    EduTrackTheme(darkTheme = true) {
        HistoryScreen(
            sessions = emptyList(),
            hasAnySessions = true,
            isFilterActive = true
        )
    }
}

@Preview(name = "History List Light", showBackground = true)
@Composable
fun HistoryListLightPreview() {
    EduTrackTheme(darkTheme = false) {
        HistoryScreen(
            sessions = listOf(
                SessionItemUiModel(
                    id = 1,
                    subjectId = 1,
                    subjectName = "Physics",
                    subjectColorHex = "#A3E635",
                    durationMinutes = 45,
                    date = LocalDate.now(),
                    notes = "Rotational kinematics problem set 4"
                ),
                SessionItemUiModel(
                    id = 2,
                    subjectId = 2,
                    subjectName = "Maths",
                    subjectColorHex = "#6C5CE7",
                    durationMinutes = 60,
                    date = LocalDate.now().minusDays(1),
                    notes = "Eigenvalues and matrix diagonalization review"
                )
            )
        )
    }
}

@Preview(name = "History List Dark", showBackground = true)
@Composable
fun HistoryListDarkPreview() {
    EduTrackTheme(darkTheme = true) {
        HistoryScreen(
            sessions = listOf(
                SessionItemUiModel(
                    id = 1,
                    subjectId = 1,
                    subjectName = "Physics",
                    subjectColorHex = "#A3E635",
                    durationMinutes = 45,
                    date = LocalDate.now(),
                    notes = "Rotational kinematics problem set 4"
                ),
                SessionItemUiModel(
                    id = 2,
                    subjectId = 2,
                    subjectName = "Maths",
                    subjectColorHex = "#6C5CE7",
                    durationMinutes = 60,
                    date = LocalDate.now().minusDays(1),
                    notes = "Eigenvalues and matrix diagonalization review"
                )
            )
        )
    }
}

@Composable
fun SessionCard(
    session: SessionItemUiModel,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val subjectColor = try {
        Color(session.subjectColorHex.toColorInt())
    } catch (_: Exception) {
        MaterialTheme.colorScheme.primary
    }

    AppCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = subjectColor.copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(subjectColor, CircleShape)
                        )
                        Text(
                            text = session.subjectName.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Duration",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = formatDurationMinutes(session.durationMinutes),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (session.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = session.notes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

fun formatDurationMinutes(minutes: Int): String {
    val hrs = minutes / 60
    val mins = minutes % 60
    return when {
        hrs > 0 && mins > 0 -> "${hrs}h ${mins}m"
        hrs > 0 -> "${hrs}h"
        else -> "${mins} min"
    }
}
