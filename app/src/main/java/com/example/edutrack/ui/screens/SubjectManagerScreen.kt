package com.example.edutrack.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.example.edutrack.R
import com.example.edutrack.data.local.Subject
import com.example.edutrack.ui.components.AppButton
import com.example.edutrack.ui.components.AppCard
import com.example.edutrack.ui.components.EmptyState
import com.example.edutrack.ui.components.MascotMood
import com.example.edutrack.ui.theme.EduTrackTheme
import com.example.edutrack.ui.viewmodel.DEFAULT_SUBJECT_COLORS
import com.example.edutrack.ui.viewmodel.SubjectFormState
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectManagerScreen(
    subjects: List<Subject>,
    formState: SubjectFormState,
    onNameChange: (String) -> Unit,
    onColorSelect: (String) -> Unit,
    onIncrementGoal: () -> Unit,
    onDecrementGoal: () -> Unit,
    onSubmitForm: ((onSuccess: (String) -> Unit) -> Unit),
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onEditSubject: (Subject) -> Unit = {},
    onDeleteSubject: (Subject, onSuccess: () -> Unit) -> Unit = { _, _ -> },
    onCancelEdit: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var subjectToDelete by remember { mutableStateOf<Subject?>(null) }

    val totalCommitmentHours = subjects.sumOf { it.weeklyGoalHours }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Subject Manager",
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
            // Weekly Commitment Card
            item {
                WeeklyCommitmentCard(totalGoalHours = totalCommitmentHours)
            }

            if (subjects.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyState(
                            mood = MascotMood.SLEEPY,
                            title = "No subjects yet",
                            message = "Add your first subject to get started tracking your study goals."
                        )
                    }
                }
            } else {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Enrolled Subjects",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Surface(
                                shape = MaterialTheme.shapes.extraLarge,
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = "${subjects.size} active",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                items(
                    items = subjects,
                    key = { it.id },
                    contentType = { "subject_card" }
                ) { subject ->
                    SubjectCard(
                        subject = subject,
                        onEditClick = { onEditSubject(subject) },
                        onDeleteClick = { subjectToDelete = subject }
                    )
                }
            }

            item {
                SubjectForm(
                    formState = formState,
                    onNameChange = onNameChange,
                    onColorSelect = onColorSelect,
                    onIncrementGoal = onIncrementGoal,
                    onDecrementGoal = onDecrementGoal,
                    onSubmitForm = {
                        onSubmitForm { message ->
                            scope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                    },
                    onCancelEdit = onCancelEdit
                )
            }
        }

        subjectToDelete?.let { targetSubject ->
            AlertDialog(
                onDismissRequest = { subjectToDelete = null },
                title = {
                    Text(
                        text = "Delete ${targetSubject.name}?",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                text = {
                    Text(
                        text = "This will permanently delete this subject and all associated study sessions.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val name = targetSubject.name
                            onDeleteSubject(targetSubject) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Deleted $name")
                                }
                            }
                            subjectToDelete = null
                        }
                    ) {
                        Text(
                            text = "Delete",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { subjectToDelete = null }) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun WeeklyCommitmentCard(
    totalGoalHours: Int,
    modifier: Modifier = Modifier
) {
    AppCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mascot_study),
                    contentDescription = "Mascot Study",
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                    modifier = Modifier.size(64.dp)
                )
                Column {
                    Text(
                        text = "Weekly Commitment",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Total scheduled focus hours",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "${totalGoalHours} hrs/wk",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun SubjectCard(
    subject: Subject,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val subjectColor = try {
        Color(subject.colorHex.toColorInt())
    } catch (_: Exception) {
        MaterialTheme.colorScheme.primary
    }

    AppCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(subjectColor, CircleShape)
                )
                Column {
                    Text(
                        text = subject.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Goal: ${subject.weeklyGoalHours}h per week",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Subject",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Subject",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun SubjectForm(
    formState: SubjectFormState,
    onNameChange: (String) -> Unit,
    onColorSelect: (String) -> Unit,
    onIncrementGoal: () -> Unit,
    onDecrementGoal: () -> Unit,
    onSubmitForm: () -> Unit,
    onCancelEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val indicatorColor = try {
        Color(formState.selectedColorHex.toColorInt())
    } catch (_: Exception) {
        MaterialTheme.colorScheme.primary
    }

    AppCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(indicatorColor, CircleShape)
                    )
                    Text(
                        text = if (formState.editingSubjectId != null) "Edit subject" else "Add new subject",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "SUBJECT NAME",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = formState.name,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Organic Chemistry") },
                isError = formState.nameError != null,
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            if (formState.nameError != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formState.nameError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "PICK COLOR TAG",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DEFAULT_SUBJECT_COLORS.forEach { colorHex ->
                    val color = Color(colorHex.toColorInt())
                    val isSelected = colorHex.equals(formState.selectedColorHex, ignoreCase = true)

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(color)
                            .then(
                                if (isSelected) {
                                    Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                } else Modifier
                            )
                            .clickable { onColorSelect(colorHex) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected Color",
                                tint = if (color.luminance() > 0.5f) Color.Black else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "WEEKLY GOAL TARGET",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                FilledIconButton(
                    onClick = onDecrementGoal,
                    modifier = Modifier.size(40.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = "-",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Text(
                    text = "${formState.weeklyGoalHours} hrs / week",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.width(20.dp))

                FilledIconButton(
                    onClick = onIncrementGoal,
                    modifier = Modifier.size(40.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase Goal",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (formState.goalError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formState.goalError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (formState.editingSubjectId != null) {
                    OutlinedButton(
                        onClick = onCancelEdit,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Text("Cancel", style = MaterialTheme.typography.labelLarge)
                    }
                }

                AppButton(
                    text = if (formState.editingSubjectId != null) "Update subject" else "Add subject",
                    onClick = onSubmitForm,
                    isAccent = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview(name = "SubjectManager Light", showBackground = true)
@Composable
fun SubjectManagerLightPreview() {
    EduTrackTheme(darkTheme = false) {
        SubjectManagerScreen(
            subjects = listOf(
                Subject(1, "Maths", "#6C5CE7", 6, LocalDate.now()),
                Subject(2, "Physics", "#A3E635", 4, LocalDate.now())
            ),
            formState = SubjectFormState(),
            onNameChange = {},
            onColorSelect = {},
            onIncrementGoal = {},
            onDecrementGoal = {},
            onSubmitForm = {},
            onBackClick = {}
        )
    }
}

@Preview(name = "SubjectManager Dark", showBackground = true)
@Composable
fun SubjectManagerDarkPreview() {
    EduTrackTheme(darkTheme = true) {
        SubjectManagerScreen(
            subjects = listOf(
                Subject(1, "Maths", "#6C5CE7", 6, LocalDate.now()),
                Subject(2, "Physics", "#A3E635", 4, LocalDate.now())
            ),
            formState = SubjectFormState(),
            onNameChange = {},
            onColorSelect = {},
            onIncrementGoal = {},
            onDecrementGoal = {},
            onSubmitForm = {},
            onBackClick = {}
        )
    }
}
