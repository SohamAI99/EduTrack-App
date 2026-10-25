package com.example.edutrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edutrack.ui.screens.SubjectManagerScreen
import com.example.edutrack.ui.theme.EduTrackTheme
import com.example.edutrack.ui.viewmodel.SubjectViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appContainer = (application as EduTrackApplication).container
        setContent {
            EduTrackTheme {
                val viewModel: SubjectViewModel = viewModel(
                    factory = SubjectViewModel.provideFactory(appContainer.repository)
                )
                val subjects by viewModel.subjects.collectAsState()
                val formState by viewModel.formState.collectAsState()

                SubjectManagerScreen(
                    subjects = subjects,
                    formState = formState,
                    onNameChange = viewModel::updateName,
                    onColorSelect = viewModel::updateColor,
                    onIncrementGoal = viewModel::incrementGoalHours,
                    onDecrementGoal = viewModel::decrementGoalHours,
                    onSubmitForm = { onSuccess ->
                        val isEdit = viewModel.formState.value.editingSubjectId != null
                        viewModel.saveSubject {
                            onSuccess(if (isEdit) "Subject updated" else "Subject saved")
                        }
                    },
                    onBackClick = {},
                    onEditSubject = viewModel::editSubject,
                    onCancelEdit = viewModel::cancelEdit
                )
            }
        }
    }
}
