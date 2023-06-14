package com.intelligentbackpack.reminderdata.storage

interface ReminderStorage {

    fun saveYear(year: String)

    fun getYear(): String

    fun saveCalendarId(calendarId: Int)

    fun getCalendarId(): Int
}
