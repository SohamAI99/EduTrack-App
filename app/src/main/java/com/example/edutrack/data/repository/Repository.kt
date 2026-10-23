package com.example.edutrack.data.repository

import com.example.edutrack.data.local.SessionDao
import com.example.edutrack.data.local.StudySession
import com.example.edutrack.data.local.Subject
import com.example.edutrack.data.local.SubjectDao
import kotlinx.coroutines.flow.Flow

class Repository(
    private val subjectDao: SubjectDao,
    private val sessionDao: SessionDao
) {
    // Subject methods
    fun getAllSubjects(): Flow<List<Subject>> = subjectDao.getAll()
    fun getSubjectById(id: Long): Flow<Subject?> = subjectDao.getById(id)
    suspend fun insertSubject(subject: Subject): Long = subjectDao.insert(subject)
    suspend fun updateSubject(subject: Subject) = subjectDao.update(subject)
    suspend fun deleteSubject(subject: Subject) = subjectDao.delete(subject)

    // Session methods
    fun getAllSessions(): Flow<List<StudySession>> = sessionDao.getAll()
    fun getSessionById(id: Long): Flow<StudySession?> = sessionDao.getById(id)
    suspend fun insertSession(session: StudySession): Long = sessionDao.insert(session)
    suspend fun updateSession(session: StudySession) = sessionDao.update(session)
    suspend fun deleteSession(session: StudySession) = sessionDao.delete(session)
}
