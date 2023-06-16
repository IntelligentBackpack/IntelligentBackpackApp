package com.intelligentbackpack.schooldata.storage

interface SchoolStorage {
    fun saveSchool(school: String, city: String)
    fun getSchool(): String
    fun getCity(): String
    fun isStudent(): Boolean
    fun saveClass(name: String)
    fun getClass(): String
    fun saveYear(year: String)

    fun getYear(): String
}
