package com.example.edutrack

import android.app.Application
import com.example.edutrack.data.AppContainer

class EduTrackApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
