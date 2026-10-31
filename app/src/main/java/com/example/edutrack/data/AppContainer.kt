package com.example.edutrack.data

import android.content.Context
import com.example.edutrack.data.local.AppDatabase
import com.example.edutrack.data.repository.Repository
import com.example.edutrack.data.repository.UserPreferencesRepository

class AppContainer(private val context: Context) {
    val database: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }

    val repository: Repository by lazy {
        Repository(database.subjectDao(), database.sessionDao())
    }

    val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context)
    }
}
