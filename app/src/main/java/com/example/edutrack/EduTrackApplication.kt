package com.example.edutrack

import android.app.Application
import com.example.edutrack.data.AppContainer

class EduTrackApplication : Application() {
    val container: AppContainer by lazy {
        AppContainer(this)
    }
}
