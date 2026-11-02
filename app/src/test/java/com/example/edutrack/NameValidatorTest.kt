package com.example.edutrack

import com.example.edutrack.util.NameValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NameValidatorTest {

    @Test
    fun isValid_emptyName_returnsFalse() {
        assertFalse(NameValidator.isValid(""))
    }

    @Test
    fun isValid_spacesOnly_returnsFalse() {
        assertFalse(NameValidator.isValid("   "))
    }

    @Test
    fun isValid_21Characters_returnsFalse() {
        val longName = "a".repeat(21)
        assertFalse(NameValidator.isValid(longName))
    }

    @Test
    fun isValid_20Characters_returnsTrue() {
        val valid20CharName = "a".repeat(20)
        assertTrue(NameValidator.isValid(valid20CharName))
    }

    @Test
    fun isValid_validName_returnsTrue() {
        assertTrue(NameValidator.isValid("Soham"))
    }

    @Test
    fun isValid_nameWithLeadingAndTrailingSpaces_returnsTrue() {
        assertTrue(NameValidator.isValid("  Sam  "))
    }
}
