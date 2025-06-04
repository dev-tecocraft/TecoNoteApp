package com.teco.note

import com.teco.note.util.isValidEmail
import org.junit.Test
import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun validEmail_returnsTrue() {
        val validEmail = "user@example.com"
        assertTrue(validEmail.isValidEmail())
    }

    @Test
    fun invalidEmail_returnsFalse() {
        val invalidEmail = "user.example"
        assertFalse(invalidEmail.isValidEmail())
    }
}