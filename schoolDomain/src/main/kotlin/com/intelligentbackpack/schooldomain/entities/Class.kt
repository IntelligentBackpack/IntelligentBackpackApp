package com.intelligentbackpack.schooldomain.entities

import com.intelligentbackpack.schooldomain.entities.implementation.ClassImpl
import com.intelligentbackpack.schooldomain.entities.person.Professor
import com.intelligentbackpack.schooldomain.entities.person.Student

/**
 * A class is a group of students that attend the same subjects taught by the same professors in the same school with the same schedule.
 *
 * @property name the name of the class
 * @property students the students that attend the class
 * @property subjects the subjects taught in the class
 * @property professors the professors that teach in the class
 * @property professorTeachSubjects the subjects taught by each professor in the class
 */
interface Class {
    val name: String
    val students: Set<Student>
    val subjects: Set<Subject>
    val professors: Set<Professor>
    val professorTeachSubjects: Map<Professor, Set<Subject>>

    /**
     * Adds a student to the class.
     *
     * @param student the student to add
     */
    fun addStudent(student: Student): Class

    /**
     * Adds a professor to the class.
     *
     * @param professor the professor to add
     * @param subjects the subjects taught by the professor in the class
     */
    fun addProfessor(professor: Professor, subjects: Set<Subject>): Class

    companion object {

        /**
         * Creates a class.
         *
         * @param name the name of the class
         * @return the created class
         * @throws IllegalArgumentException if the name is blank
         */
        fun create(name: String): Class {
            check(name.isNotBlank()) { "name cannot be blank" }
            return ClassImpl(name)
        }
    }
}

/**
 * Represents a subject.
 */
typealias Subject = String
