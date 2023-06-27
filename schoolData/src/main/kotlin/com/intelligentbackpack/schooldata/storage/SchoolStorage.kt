package com.intelligentbackpack.schooldata.storage

/**
 * Interface for the storage.
 */
interface SchoolStorage {
    /**
     * Saves the school and the city.
     *
     * @param school the school.
     * @param city the city.
     */
    fun saveSchool(school: String, city: String)

    /**
     * Returns the school.
     *
     * @return the school.
     */
    fun getSchool(): String

    /**
     * Returns the city of the school.
     *
     * @return the city.
     */
    fun getCity(): String

    /**
     * Checks if the user is a student.
     *
     * @return true if the user is a student, false otherwise.
     */
    fun isStudent(): Boolean

    /**
     * Saves the student class.
     *
     * @param className the name of the class.
     */
    fun saveClass(className: String)

    /**
     * Gets the student class.
     *
     * @return the name of the class.
     */
    fun getClass(): String

    /**
     * Saves the school year.
     *
     * @param year the year to save.
     */
    fun saveYear(year: String)

    /**
     * Gets the school year.
     *
     * @return the year.
     */
    fun getYear(): String
}
