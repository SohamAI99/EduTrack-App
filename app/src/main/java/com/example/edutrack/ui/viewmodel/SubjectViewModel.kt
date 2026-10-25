package com.example.edutrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.edutrack.data.local.Subject
import com.example.edutrack.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

val DEFAULT_SUBJECT_COLORS = listOf(
    "#D4FF00", // Lime
    "#8C7CFF", // Purple
    "#A29BFE", // Lavender
    "#38B6FF", // Blue
    "#FF7034", // Orange
    "#FF9E9E", // Pink
    "#FFB366", // Peach
    "#E2E8F0"  // White/Ice
)

data class SubjectFormState(
    val editingSubjectId: Long? = null,
    val name: String = "",
    val selectedColorHex: String = DEFAULT_SUBJECT_COLORS.first(),
    val weeklyGoalHours: Int = 4,
    val nameError: String? = null,
    val goalError: String? = null
)

class SubjectViewModel(private val repository: Repository) : ViewModel() {

    val subjects: StateFlow<List<Subject>> = repository.getAllSubjects()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _formState = MutableStateFlow(SubjectFormState())
    val formState: StateFlow<SubjectFormState> = _formState.asStateFlow()

    fun updateName(name: String) {
        _formState.update { it.copy(name = name, nameError = null) }
    }

    fun updateColor(colorHex: String) {
        _formState.update { it.copy(selectedColorHex = colorHex) }
    }

    fun updateGoalHours(hours: Int) {
        val clampedHours = hours.coerceIn(1, 168)
        _formState.update { it.copy(weeklyGoalHours = clampedHours, goalError = null) }
    }

    fun incrementGoalHours() {
        updateGoalHours(_formState.value.weeklyGoalHours + 1)
    }

    fun decrementGoalHours() {
        updateGoalHours(_formState.value.weeklyGoalHours - 1)
    }

    fun resetForm() {
        _formState.value = SubjectFormState()
    }

    fun editSubject(subject: Subject) {
        _formState.value = SubjectFormState(
            editingSubjectId = subject.id,
            name = subject.name,
            selectedColorHex = subject.colorHex,
            weeklyGoalHours = subject.weeklyGoalHours
        )
    }

    fun cancelEdit() {
        resetForm()
    }

    fun validateForm(): Boolean {
        val current = _formState.value
        val trimmedName = current.name.trim()

        var nameErr: String? = null
        var goalErr: String? = null

        if (trimmedName.isEmpty()) {
            nameErr = "Subject name is required"
        } else if (trimmedName.length > 30) {
            nameErr = "Name must be 30 characters or less"
        }

        if (current.weeklyGoalHours !in 1..168) {
            goalErr = "Goal must be between 1 and 168 hours"
        }

        val isValid = nameErr == null && goalErr == null

        _formState.update {
            it.copy(
                nameError = nameErr,
                goalError = goalErr
            )
        }

        return isValid
    }

    fun saveSubject(onSuccess: (() -> Unit)? = null) {
        if (!validateForm()) return

        val current = _formState.value
        val subject = Subject(
            id = current.editingSubjectId ?: 0,
            name = current.name.trim(),
            colorHex = current.selectedColorHex,
            weeklyGoalHours = current.weeklyGoalHours,
            createdAt = LocalDate.now()
        )
        viewModelScope.launch {
            if (current.editingSubjectId == null) {
                repository.insertSubject(subject)
            } else {
                repository.updateSubject(subject)
            }
            resetForm()
            onSuccess?.invoke()
        }
    }

    companion object {
        fun provideFactory(repository: Repository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SubjectViewModel(repository) as T
                }
            }
    }
}
