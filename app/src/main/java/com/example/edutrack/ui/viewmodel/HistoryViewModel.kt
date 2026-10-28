package com.example.edutrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.edutrack.data.repository.Repository
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

class HistoryViewModel(private val repository: Repository) : ViewModel() {

    val uiState: StateFlow<List<SessionItemUiModel>> = combine(
        repository.getAllSessions(),
        repository.getAllSubjects()
    ) { sessions, subjects ->
        val subjectMap = subjects.associateBy { it.id }
        sessions.map { session ->
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
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

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
