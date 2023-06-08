package com.teco.note.util

import java.util.Calendar
import java.util.Date

object NoteAppUtil {
    fun String.getGreetingMessage(): String {
        val date = Date()
        val cal: Calendar = Calendar.getInstance()
        cal.time = date
        return when (cal.get(Calendar.HOUR_OF_DAY)) {
            in 12..16 -> {
                "Good Afternoon, $this!"
            }
            in 17..20 -> {
                "Good Evening, $this!"
            }
            in 21..23 -> {
                "Good Night, $this!"
            }
            else -> {
                "Good Morning, $this!"
            }
        }
    }
}