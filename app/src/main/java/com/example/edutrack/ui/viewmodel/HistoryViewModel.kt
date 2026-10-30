package com.example.edutrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.edutrack.data.local.Subject
import com.example.edutrack.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

data class SessionItemUiModel(
    val id: Long,
    val subjectId: Long,
    val subjectName: String,
    val subjectColorHex: String,
    val durationMinutes: Int,
    val date: LocalDate,
    val notes: String
)

data class HistoryUiState(
    val hasAnySessions: Boolean = false,
    val filteredSessions: List<SessionItemUiModel> = emptyList(),
    val subjects: List<Subject> = emptyList(),
    val selectedSubjectId: Long? = null
)

class HistoryViewModel(private val repository: Repository) : ViewModel() {

    private val _selectedSubjectId = MutableStateFlow<Long?>(null)

    val uiState: StateFlow<HistoryUiState> = combine(
        repository.getAllSessions(),
        repository.getAllSubjects(),
        _selectedSubjectId
    ) { sessions, subjects, selectedSubjectId ->
        val validSubjectId = if (selectedSubjectId != null && subjects.none { it.id == selectedSubjectId }) {
            null
        } else {
            selectedSubjectId
        }

        val subjectMap = subjects.associateBy { it.id }

        val filteredSessions = sessions.filter { session ->
            validSubjectId == null || session.subjectId == validSubjectId
        }.map { session ->
            val subject = subjectMap[session.subjectId]
            SessionItemUiModel(
                id = session.id,
                subjectId = session.subjectId,
                subjectName = subject?.name ?: "Unknown Subject",
                subjectColorHex = subject?.colorHex ?: "#6C5CE7",
                durationMinutes = session.durationMinutes,
                date = session.date,
                notes = session.notes
            )
        }

        HistoryUiState(
            hasAnySessions = sessions.isNotEmpty(),
            filteredSessions = filteredSessions,
            subjects = subjects,
            selectedSubjectId = validSubjectId
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryUiState()
    )

    fun selectSubjectFilter(subjectId: Long?) {
        _selectedSubjectId.value = subjectId
    }

    companion object {
        fun provideFactory(repository: Repository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HistoryViewModel(repository) as T
                }
            }
    }
}
