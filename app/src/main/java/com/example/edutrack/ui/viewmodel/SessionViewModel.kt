package com.example.edutrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.edutrack.data.local.StudySession
import com.example.edutrack.data.local.Subject
import com.example.edutrack.data.model.SessionWithSubject
import com.example.edutrack.data.repository.Repository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class TimeRangeFilter(val label: String, val days: Int?) {
    ALL("All Time", null),
    DAYS_7("7 Days", 7),
    DAYS_14("14 Days", 14),
    DAYS_30("30 Days", 30)
}

class SessionViewModel(private val repository: Repository) : ViewModel() {

    val subjects: StateFlow<List<Subject>> = repository.getAllSubjects()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allSessions: StateFlow<List<StudySession>> = repository.getAllSessions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // History Filters
    val selectedSubjectFilter = MutableStateFlow<Long?>(null)
    val selectedTimeRangeFilter = MutableStateFlow(TimeRangeFilter.DAYS_14)

    val filteredSessions: StateFlow<List<SessionWithSubject>> = combine(
        allSessions,
        subjects,
        selectedSubjectFilter,
        selectedTimeRangeFilter
    ) { sessions, subjectsList, subjectId, timeRange ->
        val subjectsMap = subjectsList.associateBy { it.id }
        val today = LocalDate.now()

        sessions
            .filter { session ->
                val matchesSubject = subjectId == null || session.subjectId == subjectId
                val matchesTime = if (timeRange.days != null) {
                    !session.date.isBefore(today.minusDays(timeRange.days.toLong()))
                } else true
                matchesSubject && matchesTime
            }
            .map { session ->
                SessionWithSubject(session, subjectsMap[session.subjectId])
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val totalMinutesInFilter: StateFlow<Int> = filteredSessions.map { list ->
        list.sumOf { it.session.durationMinutes }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    // Form State (Add / Edit)
    var editingSessionId: Long? = null
        private set

    val formSubjectId = MutableStateFlow<Long?>(null)
    val formDurationMinutes = MutableStateFlow(45)
    val formDate = MutableStateFlow(LocalDate.now())
    val formNotes = MutableStateFlow("")
    val formTag = MutableStateFlow("Practice Problems")

    val subjectError = MutableStateFlow<String?>(null)
    val durationError = MutableStateFlow<String?>(null)

    fun prepareForAdd(preselectedSubjectId: Long? = null) {
        editingSessionId = null
        formSubjectId.value = preselectedSubjectId ?: subjects.value.firstOrNull()?.id
        formDurationMinutes.value = 45
        formDate.value = LocalDate.now()
        formNotes.value = ""
        formTag.value = "Practice Problems"
        subjectError.value = null
        durationError.value = null
    }

    fun prepareForEdit(sessionId: Long) {
        viewModelScope.launch {
            val session = repository.getSessionById(sessionId).firstOrNull() ?: return@launch
            editingSessionId = session.id
            formSubjectId.value = session.subjectId
            formDurationMinutes.value = session.durationMinutes
            formDate.value = session.date
            formNotes.value = session.notes
            formTag.value = session.tag.ifEmpty { "Practice Problems" }
            subjectError.value = null
            durationError.value = null
        }
    }

    fun setSubject(subjectId: Long) {
        formSubjectId.value = subjectId
        subjectError.value = null
    }

    fun setDuration(minutes: Int) {
        formDurationMinutes.value = minutes
        if (minutes in 1..720) {
            durationError.value = null
        } else {
            durationError.value = "Duration must be between 1 and 720 minutes."
        }
    }

    fun setDate(date: LocalDate) {
        formDate.value = date
    }

    fun setNotes(notes: String) {
        formNotes.value = notes
    }

    fun setTag(tag: String) {
        formTag.value = tag
    }

    fun validateAndSave(onSuccess: (String) -> Unit): Boolean {
        var isValid = true

        val subjectId = formSubjectId.value
        if (subjectId == null) {
            subjectError.value = "Please select a course or subject."
            isValid = false
        } else {
            subjectError.value = null
        }

        val duration = formDurationMinutes.value
        if (duration !in 1..720) {
            durationError.value = "Duration must be between 1 and 720 minutes."
            isValid = false
        } else {
            durationError.value = null
        }

        if (!isValid || subjectId == null) return false

        viewModelScope.launch {
            val sessionToSave = StudySession(
                id = editingSessionId ?: 0,
                subjectId = subjectId,
                durationMinutes = duration,
                date = formDate.value,
                notes = formNotes.value.trim(),
                tag = formTag.value,
                createdAt = formDate.value
            )

            if (editingSessionId != null) {
                repository.updateSession(sessionToSave)
                onSuccess("Study session updated!")
            } else {
                repository.insertSession(sessionToSave)
                onSuccess("Study session logged successfully!")
            }
        }
        return true
    }

    fun deleteSession(sessionId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteSessionById(sessionId)
            onSuccess()
        }
    }

    fun getSessionDetail(sessionId: Long): Flow<SessionWithSubject?> {
        return combine(
            repository.getSessionById(sessionId),
            repository.getAllSubjects()
        ) { session, subjectsList ->
            if (session == null) null
            else {
                val subject = subjectsList.firstOrNull { it.id == session.subjectId }
                SessionWithSubject(session, subject)
            }
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
