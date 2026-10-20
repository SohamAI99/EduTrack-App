package com.example.edutrack.ui.components

import androidx.annotation.DrawableRes
import com.example.edutrack.R

enum class MascotMood(@get:DrawableRes val drawableRes: Int) {
    HAPPY(R.drawable.mascot_happy),
    CELEBRATE(R.drawable.mascot_celebrate),
    SLEEPY(R.drawable.mascot_sleepy),
    STUDY(R.drawable.mascot_study)
}
