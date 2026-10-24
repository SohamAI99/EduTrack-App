package com.example.edutrack.data

import android.content.Context
import com.example.edutrack.data.local.AppDatabase
import com.example.edutrack.data.repository.Repository

class AppContainer(context: Context) {
    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }

    val repository: Repository by lazy {
        Repository(database.subjectDao(), database.sessionDao())
    }

    val userPreferencesRepository: com.example.edutrack.data.repository.UserPreferencesRepository by lazy {
        com.example.edutrack.data.repository.UserPreferencesRepository(context)
    }
}
