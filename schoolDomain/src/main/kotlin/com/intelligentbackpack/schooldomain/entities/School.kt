package com.intelligentbackpack.schooldomain.entities

import com.intelligentbackpack.schooldomain.entities.calendar.SchoolCalendar
import com.intelligentbackpack.schooldomain.entities.implementation.SchoolImpl
import com.intelligentbackpack.schooldomain.entities.person.Professor
import com.intelligentbackpack.schooldomain.entities.person.Student

/**
 * Represents a school.
 *
 * @property name the name of the school
 * @property city the city where the school is located
 * @property classes the classes that are taught in the school
 * @property students the students that attend the school
 * @property professors the professors that teach in the school
 * @property calendar the calendar of the school
 */
interface School {
    val name: String
    val city: String
    val classes: Set<Class>
    val students: Set<Student>
    val professors: Set<Professor>
    val calendar: SchoolCalendar?

    /**
     * Adds a class to the school.
     *
     * @param classToAdd the class to add
     */
    fun addClass(classToAdd: Class): School

    /**
     * Adds a student to the school.
     *
     * @param student the student to add
     */
    fun addStudent(student: Student): School

    /**
     * Adds a professor to the school.
     *
     * @param professor the professor to add
     */
    fun addProfessor(professor: Professor): School

    /**
     * Replaces the calendar of the school.
     *
     * @param calendar the new calendar
     */
    fun replaceCalendar(calendar: SchoolCalendar): School

    companion object {
        /**
         * Creates a school.
         *
         * @param name the name of the school
         * @param city the city where the school is located
         * @return the created school
         * @throws IllegalArgumentException if the name or the city is blank
         */
        fun create(name: String, city: String): School {
            check(name.isBlank()) { "name cannot be blank" }
            check(city.isBlank()) { "city cannot be blank" }
            return SchoolImpl(name, city)
        }
    }
}
