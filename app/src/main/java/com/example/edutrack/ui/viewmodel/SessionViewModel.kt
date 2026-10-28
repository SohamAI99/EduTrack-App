package com.example.edutrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.edutrack.data.local.Subject
import com.example.edutrack.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

data class SessionFormState(
    val selectedSubject: Subject? = null,
    val durationMinutesText: String = "45",
    val date: LocalDate = LocalDate.now(),
    val notesText: String = "",
    val subjectError: String? = null,
    val durationError: String? = null,
    val dateError: String? = null
)

class SessionViewModel(private val repository: Repository) : ViewModel() {

    private val _formState = MutableStateFlow(SessionFormState())
    val formState: StateFlow<SessionFormState> = _formState.asStateFlow()

    fun updateSelectedSubject(subject: Subject?) {
        _formState.update { it.copy(selectedSubject = subject, subjectError = null) }
    }

    fun updateDurationText(durationText: String) {
        if (durationText.isEmpty() || durationText.all { it.isDigit() }) {
            _formState.update { it.copy(durationMinutesText = durationText, durationError = null) }
        }
    }

    fun updateDate(date: LocalDate) {
        _formState.update { it.copy(date = date, dateError = null) }
    }

    fun updateNotesText(notesText: String) {
        _formState.update { it.copy(notesText = notesText) }
    }

    fun resetForm(defaultSubject: Subject? = null) {
        _formState.value = SessionFormState(selectedSubject = defaultSubject)
    }

    companion object {
        fun provideFactory(repository: Repository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SessionViewModel(repository) as T
                }
            }
    }
}
