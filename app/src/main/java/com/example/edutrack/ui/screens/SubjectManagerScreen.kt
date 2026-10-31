package com.example.edutrack.ui.screens

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
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
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
import com.example.edutrack.ui.viewmodel.DEFAULT_SUBJECT_COLORS
import com.example.edutrack.ui.viewmodel.SubjectFormState
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
                items(subjects) { subject ->
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
                        text = "All study sessions for this subject will also be deleted.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val target = targetSubject
                            subjectToDelete = null
                            onDeleteSubject(target) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Subject deleted")
                                }
                            }
                        }
                    ) {
                        Text(
                            text = "Delete",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { subjectToDelete = null }
                    ) {
                        Text(text = "Cancel")
                    }
                }
            )
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
    onCancelEdit: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    AppCard(
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val indicatorColor = try {
                        Color(formState.selectedColorHex.toColorInt())
                    } catch (_: Exception) {
                        MaterialTheme.colorScheme.secondary
                    }
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(indicatorColor, CircleShape)
                    )
                    Text(
                        text = if (formState.editingSubjectId != null) "Edit subject" else "Add new subject",
                        style = MaterialTheme.typography.titleMedium,
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
                singleLine = true,
                placeholder = {
                    Text(
                        text = "e.g. Organic Chemistry",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    Text(
                        text = "📖",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                isError = formState.nameError != null,
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
            )
            if (formState.nameError != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formState.nameError,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "PICK COLOR TAG",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val row1 = DEFAULT_SUBJECT_COLORS.take(4)
                val row2 = DEFAULT_SUBJECT_COLORS.drop(4)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row1.forEach { hex ->
                        ColorCircle(
                            colorHex = hex,
                            isSelected = hex.equals(formState.selectedColorHex, ignoreCase = true),
                            onClick = { onColorSelect(hex) }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row2.forEach { hex ->
                        ColorCircle(
                            colorHex = hex,
                            isSelected = hex.equals(formState.selectedColorHex, ignoreCase = true),
                            onClick = { onColorSelect(hex) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "WEEKLY GOAL TARGET",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FilledIconButton(
                        onClick = onDecrementGoal,
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(
                            text = "-",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${formState.weeklyGoalHours}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "hrs / week",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    FilledIconButton(
                        onClick = onIncrementGoal,
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Increase goal")
                    }
                }
            }
            if (formState.goalError != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formState.goalError,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

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
                    text = if (formState.editingSubjectId != null) "Save changes" else "Add subject",
                    onClick = onSubmitForm,
                    isAccent = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ColorCircle(
    colorHex: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = try {
        Color(colorHex.toColorInt())
    } catch (_: Exception) {
        MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = Modifier
            .size(42.dp)
            .then(
                if (isSelected) {
                    Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                } else {
                    Modifier
                }
            )
            .padding(4.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = if (color.luminance() > 0.5f) Color.Black else Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun SubjectCard(
    subject: Subject,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val color = try {
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
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(color, CircleShape)
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Goal: ${subject.weeklyGoalHours} hrs per week",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit subject",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete subject",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(name = "Subject List Light", showBackground = true)
@Composable
fun SubjectListLightPreview() {
    EduTrackTheme(darkTheme = false) {
        SubjectManagerScreen(
            subjects = listOf(
                Subject(id = 1, name = "Computer Science", colorHex = "#6C5CE7", weeklyGoalHours = 6, createdAt = LocalDate.now()),
                Subject(id = 2, name = "Advanced Calculus", colorHex = "#00CEC9", weeklyGoalHours = 4, createdAt = LocalDate.now())
            ),
            formState = SubjectFormState(),
            onNameChange = {},
            onColorSelect = {},
            onIncrementGoal = {},
            onDecrementGoal = {},
            onSubmitForm = { _ -> },
            onBackClick = {}
        )
    }
}

@Preview(name = "Subject List Dark", showBackground = true)
@Composable
fun SubjectListDarkPreview() {
    EduTrackTheme(darkTheme = true) {
        SubjectManagerScreen(
            subjects = listOf(
                Subject(id = 1, name = "Computer Science", colorHex = "#6C5CE7", weeklyGoalHours = 6, createdAt = LocalDate.now()),
                Subject(id = 2, name = "Advanced Calculus", colorHex = "#00CEC9", weeklyGoalHours = 4, createdAt = LocalDate.now())
            ),
            formState = SubjectFormState(),
            onNameChange = {},
            onColorSelect = {},
            onIncrementGoal = {},
            onDecrementGoal = {},
            onSubmitForm = { _ -> },
            onBackClick = {}
        )
    }
}

@Preview(name = "Subject Form Empty Light", showBackground = true)
@Composable
fun SubjectFormEmptyLightPreview() {
    EduTrackTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            SubjectForm(
                formState = SubjectFormState(),
                onNameChange = {},
                onColorSelect = {},
                onIncrementGoal = {},
                onDecrementGoal = {},
                onSubmitForm = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Subject Form Empty Dark", showBackground = true)
@Composable
fun SubjectFormEmptyDarkPreview() {
    EduTrackTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            SubjectForm(
                formState = SubjectFormState(),
                onNameChange = {},
                onColorSelect = {},
                onIncrementGoal = {},
                onDecrementGoal = {},
                onSubmitForm = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Subject Form Error Light", showBackground = true)
@Composable
fun SubjectFormErrorLightPreview() {
    EduTrackTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            SubjectForm(
                formState = SubjectFormState(
                    name = "",
                    nameError = "Subject name is required",
                    goalError = "Goal must be between 1 and 168 hours"
                ),
                onNameChange = {},
                onColorSelect = {},
                onIncrementGoal = {},
                onDecrementGoal = {},
                onSubmitForm = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Subject Form Error Dark", showBackground = true)
@Composable
fun SubjectFormErrorDarkPreview() {
    EduTrackTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            SubjectForm(
                formState = SubjectFormState(
                    name = "",
                    nameError = "Subject name is required",
                    goalError = "Goal must be between 1 and 168 hours"
                ),
                onNameChange = {},
                onColorSelect = {},
                onIncrementGoal = {},
                onDecrementGoal = {},
                onSubmitForm = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Subject Form Edit Light", showBackground = true)
@Composable
fun SubjectFormEditLightPreview() {
    EduTrackTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            SubjectForm(
                formState = SubjectFormState(
                    editingSubjectId = 1L,
                    name = "Computer Science",
                    selectedColorHex = "#6C5CE7",
                    weeklyGoalHours = 6
                ),
                onNameChange = {},
                onColorSelect = {},
                onIncrementGoal = {},
                onDecrementGoal = {},
                onSubmitForm = {},
                onCancelEdit = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Subject Form Edit Dark", showBackground = true)
@Composable
fun SubjectFormEditDarkPreview() {
    EduTrackTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            SubjectForm(
                formState = SubjectFormState(
                    editingSubjectId = 1L,
                    name = "Computer Science",
                    selectedColorHex = "#6C5CE7",
                    weeklyGoalHours = 6
                ),
                onNameChange = {},
                onColorSelect = {},
                onIncrementGoal = {},
                onDecrementGoal = {},
                onSubmitForm = {},
                onCancelEdit = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
