package com.example.edutrack.util

/**
 * Pure Kotlin validator for user name inputs.
 */
object NameValidator {

    /**
     * Validates that a user name is non-empty after trimming and does not exceed 20 characters.
     *
     * @param name Input string to validate.
     * @return `true` if valid, `false` otherwise.
     */
    fun isValid(name: String): Boolean {
        val trimmed = name.trim()
        return trimmed.isNotEmpty() && trimmed.length <= 20
    }
}
