package com.intelligentbackpack.schooldomain.entities.implementation

import com.intelligentbackpack.schooldomain.entities.Class
import com.intelligentbackpack.schooldomain.entities.School
import com.intelligentbackpack.schooldomain.entities.calendar.SchoolCalendar
import com.intelligentbackpack.schooldomain.entities.person.Professor
import com.intelligentbackpack.schooldomain.entities.person.Student

/**
 * A school.
 *
 * @property name the name of the school
 * @property city the city where the school is located
 * @property calendar the calendar of the school
 * @property classes the classes taught in the school
 * @property students the students that attend the school
 * @property professors the professors that teach in the school
 */
internal data class SchoolImpl(
    override val name: String,
    override val city: String,
) : School {

    override var calendar: SchoolCalendar? = null
        private set

    override var classes: Set<Class> = setOf()
        private set
    override var students: Set<Student> = setOf()
        private set
    override var professors: Set<Professor> = setOf()
        private set

    private fun copy(
        calendar: SchoolCalendar? = this.calendar,
        classes: Set<Class> = this.classes,
        students: Set<Student> = this.students,
        professors: Set<Professor> = this.professors,
    ): SchoolImpl =
        SchoolImpl(name, city).apply {
            this.calendar = calendar
            this.classes = classes
            this.students = students
            this.professors = professors
        }

    /**
     * Adds a class to the school.
     *
     * @param classToAdd the class to add
     * @throws IllegalArgumentException if the class is already in the school
     */
    override fun addClass(classToAdd: Class): School {
        if (classes.contains(classToAdd)) {
            throw IllegalArgumentException("class already in school")
        } else {
            return copy(classes = classes + classToAdd)
        }
    }

    /**
     * Adds a student to the school.
     *
     * @param student the student to add
     * @throws IllegalArgumentException if the student is already in the school
     */
    override fun addStudent(student: Student): School {
        if (students.contains(student)) {
            throw IllegalArgumentException("student already in school")
        } else {
            return copy(students = students + student)
        }
    }

    /**
     * Adds a professor to the school.
     *
     * @param professor the professor to add
     * @throws IllegalArgumentException if the professor is already in the school
     */
    override fun addProfessor(professor: Professor): School {
        if (professors.contains(professor)) {
            throw IllegalArgumentException("professor already in school")
        } else {
            return copy(professors = professors + professor)
        }
    }

    /**
     * Replaces the calendar of the school.
     *
     * @param calendar the new calendar of the school
     */
    override fun replaceCalendar(calendar: SchoolCalendar): School =
        copy(calendar = calendar)
}
