package com.example.edutrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.edutrack.data.local.StudySession
import com.example.edutrack.data.local.Subject
import com.example.edutrack.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class SessionFormState(
    val editingSessionId: Long? = null,
    val editingCreatedAt: LocalDate? = null,
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

    fun loadSessionForEditing(sessionId: Long) {
        if (sessionId == -1L) {
            resetForm()
            return
        }
        viewModelScope.launch {
            val session = repository.getSessionById(sessionId).firstOrNull() ?: return@launch
            val subjects = repository.getAllSubjects().firstOrNull() ?: emptyList()
            val subject = subjects.find { it.id == session.subjectId }

            _formState.value = SessionFormState(
                editingSessionId = session.id,
                editingCreatedAt = session.createdAt,
                selectedSubject = subject,
                durationMinutesText = session.durationMinutes.toString(),
                date = session.date,
                notesText = session.notes
            )
        }
    }

    fun validateForm(): Boolean {
        val current = _formState.value
        var subjectErr: String? = null
        var durationErr: String? = null
        var dateErr: String? = null

        if (current.selectedSubject == null) {
            subjectErr = "A subject must be selected"
        }

        val durationInt = current.durationMinutesText.toIntOrNull()
        if (durationInt == null || durationInt !in 1..720) {
            durationErr = "Duration must be between 1 and 720 minutes"
        }

        if (current.date.isAfter(LocalDate.now())) {
            dateErr = "Date cannot be in the future"
        }

        val isValid = subjectErr == null && durationErr == null && dateErr == null

        _formState.update {
            it.copy(
                subjectError = subjectErr,
                durationError = durationErr,
                dateError = dateErr
            )
        }

        return isValid
    }

    fun saveSession(onSuccess: () -> Unit) {
        if (!validateForm()) return

        val current = _formState.value
        val subject = current.selectedSubject ?: return
        val durationMinutes = current.durationMinutesText.toIntOrNull() ?: return

        val session = StudySession(
            id = current.editingSessionId ?: 0L,
            subjectId = subject.id,
            durationMinutes = durationMinutes,
            date = current.date,
            notes = current.notesText.trim(),
            createdAt = current.editingCreatedAt ?: LocalDate.now()
        )

        viewModelScope.launch {
            if (current.editingSessionId == null) {
                repository.insertSession(session)
            } else {
                repository.updateSession(session)
            }
            resetForm(defaultSubject = subject)
            onSuccess()
        }
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
