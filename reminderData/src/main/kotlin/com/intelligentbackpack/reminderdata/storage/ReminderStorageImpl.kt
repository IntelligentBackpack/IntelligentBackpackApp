package com.intelligentbackpack.reminderdata.storage

import android.content.Context
import android.content.SharedPreferences

class ReminderStorageImpl(private val context: Context) : ReminderStorage {

    private val name = "IntelligentBackpackReminderSharedPref"
    override fun saveYear(year: String) {
        val sp: SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        val edit: SharedPreferences.Editor = sp.edit()
        edit.putString("year", year)
        edit.apply()
    }

    override fun getYear(): String {
        val sp = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        return sp.getString("year", "")!!
    }

    override fun saveCalendarId(calendarId: Int) {
        val sp: SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        val edit: SharedPreferences.Editor = sp.edit()
        edit.putString("calendarId", calendarId.toString())
        edit.apply()
    }

    override fun getCalendarId(): Int {
        val sp = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        return sp.getString("calendarId", "")!!.toInt()
    }
}
