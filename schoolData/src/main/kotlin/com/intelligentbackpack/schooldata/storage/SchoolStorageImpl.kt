package com.intelligentbackpack.schooldata.storage

import android.content.Context

class SchoolStorageImpl(private val context: Context) : SchoolStorage {
    private val name = "IntelligentBackpackSchoolSharedPref"

    override fun saveSchool(school: String, city: String) {
        val sp = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        val edit = sp.edit()
        edit.putString("school", school)
        edit.putString("city", city)
        edit.apply()
    }

    override fun getSchool(): String {
        val sp = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        return sp.getString("school", "")!!
    }

    override fun getCity(): String {
        val sp = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        return sp.getString("city", "")!!
    }

    override fun isStudent(): Boolean {
        val sp = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        return sp.contains("class")
    }

    override fun saveClass(className: String) {
        val sp = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        val edit = sp.edit()
        edit.putString("class", className)
        edit.apply()
    }

    override fun getClass(): String {
        val sp = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        return sp.getString("class", "")!!
    }

    override fun saveYear(year: String) {
        val sp = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        val edit = sp.edit()
        edit.putString("year", year)
        edit.apply()
    }

    override fun getYear(): String {
        val sp = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        return sp.getString("year", "")!!
    }
}
