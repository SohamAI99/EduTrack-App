package com.example.edutrack.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert
    suspend fun insert(session: StudySession): Long

    @Update
    suspend fun update(session: StudySession)

    @Delete
    suspend fun delete(session: StudySession)

    @Query("SELECT * FROM study_sessions ORDER BY date DESC, id DESC")
    fun getAll(): Flow<List<StudySession>>

    @Query("SELECT * FROM study_sessions WHERE id = :id")
    fun getById(id: Long): Flow<StudySession?>
}
