package com.example.edutrack.ui.viewmodel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.edutrack.data.local.StudySession
import com.example.edutrack.data.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

@Immutable
data class SessionDetailUiModel(
    val id: Long,
    val subjectId: Long,
    val subjectName: String,
    val subjectColorHex: String,
    val durationMinutes: Int,
    val date: LocalDate,
    val notes: String
)

class SessionDetailViewModel(
    private val sessionId: Long,
    private val repository: Repository
) : ViewModel() {

    val uiState: StateFlow<SessionDetailUiModel?> = combine(
        repository.getSessionById(sessionId),
        repository.getAllSubjects()
    ) { session, subjects ->
        if (session == null) {
            null
        } else {
            val subject = subjects.find { it.id == session.subjectId }
            SessionDetailUiModel(
                id = session.id,
                subjectId = session.subjectId,
                subjectName = subject?.name ?: "Unknown Subject",
                subjectColorHex = subject?.colorHex ?: "#6C5CE7",
                durationMinutes = session.durationMinutes,
                date = session.date,
                notes = session.notes
            )
        }
    }
        .flowOn(Dispatchers.Default)
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun deleteSession(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val session = repository.getSessionById(sessionId).firstOrNull()
            if (session != null) {
                repository.deleteSession(session)
            }
            onSuccess()
        }
    }

    companion object {
        fun provideFactory(sessionId: Long, repository: Repository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SessionDetailViewModel(sessionId, repository) as T
                }
            }
    }
}
