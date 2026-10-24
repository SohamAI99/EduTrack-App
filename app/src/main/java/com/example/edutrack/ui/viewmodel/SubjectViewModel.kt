package com.example.edutrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.edutrack.data.local.Subject
import com.example.edutrack.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class SubjectViewModel(private val repository: Repository) : ViewModel() {

    val subjects: StateFlow<List<Subject>> = repository.getAllSubjects()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val totalWeeklyGoalHours: StateFlow<Int> = subjects.map { list ->
        list.sumOf { it.weeklyGoalHours }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    // Form / Dialog State
    var editingSubjectId: Long? = null
        private set

    val formName = MutableStateFlow("")
    val formColorHex = MutableStateFlow("#6C5CE7")
    val formWeeklyGoalHours = MutableStateFlow(4)
    val nameError = MutableStateFlow<String?>(null)

    val availableColors = listOf(
        "#6C5CE7", // Purple
        "#00CEC9", // Cyan
        "#00B894", // Green
        "#E17055", // Coral
        "#FDCB6E", // Yellow
        "#FD79A8", // Pink
        "#0984E3", // Blue
        "#E84393"  // Magenta
    )

    fun resetForm() {
        editingSubjectId = null
        formName.value = ""
        formColorHex.value = availableColors.first()
        formWeeklyGoalHours.value = 4
        nameError.value = null
    }

    fun prepareForEdit(subject: Subject) {
        editingSubjectId = subject.id
        formName.value = subject.name
        formColorHex.value = subject.colorHex
        formWeeklyGoalHours.value = subject.weeklyGoalHours
        nameError.value = null
    }

    fun setName(name: String) {
        formName.value = name
        if (name.isNotBlank()) {
            nameError.value = null
        }
    }

    fun setColorHex(color: String) {
        formColorHex.value = color
    }

    fun setWeeklyGoalHours(hours: Int) {
        formWeeklyGoalHours.value = hours.coerceIn(1, 40)
    }

    fun validateAndSave(onSuccess: (String) -> Unit): Boolean {
        val trimmedName = formName.value.trim()
        if (trimmedName.isBlank()) {
            nameError.value = "Subject name cannot be empty."
            return false
        }
        nameError.value = null

        viewModelScope.launch {
            val subjectToSave = Subject(
                id = editingSubjectId ?: 0,
                name = trimmedName,
                colorHex = formColorHex.value,
                weeklyGoalHours = formWeeklyGoalHours.value,
                createdAt = LocalDate.now()
            )

            if (editingSubjectId != null) {
                repository.updateSubject(subjectToSave)
                onSuccess("Subject updated successfully!")
            } else {
                repository.insertSubject(subjectToSave)
                onSuccess("Subject added!")
            }
            resetForm()
        }
        return true
    }

    fun deleteSubject(subject: Subject, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteSubject(subject)
            onSuccess()
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
