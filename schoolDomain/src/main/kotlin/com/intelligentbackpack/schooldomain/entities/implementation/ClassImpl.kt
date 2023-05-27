package com.intelligentbackpack.schooldomain.entities.implementation

import com.intelligentbackpack.schooldomain.entities.Class
import com.intelligentbackpack.schooldomain.entities.School
import com.intelligentbackpack.schooldomain.entities.Subject
import com.intelligentbackpack.schooldomain.entities.person.Professor
import com.intelligentbackpack.schooldomain.entities.person.Student

/**
 * A class is a group of students that attend the same subjects taught by the same professors in the same school with the same schedule.
 *
 * @property name the name of the class
 * @property school the school where the class is taught
 * @property students the students that attend the class
 * @property subjects the subjects taught in the class
 * @property professors the professors that teach in the class
 * @property professorTeachSubjects the subjects taught by each professor in the class
 */
internal data class ClassImpl(
    override val name: String,
    override val school: School,
) : Class {
    override var students: Set<Student> = setOf()
        private set
    override val subjects: Set<Subject>
        get() = professorTeachSubjects.values.flatten().toSet()
    override var professors: Set<Professor> = setOf()
        private set
    override var professorTeachSubjects: Map<Professor, Set<Subject>> = mapOf()
        private set

    /**
     * Adds a student to the class.
     *
     * @param student the student to add
     * @throws IllegalArgumentException if the student is already in the class or if the student is not in the class
     */
    override fun addStudent(student: Student) {
        if (students.contains(student)) {
            throw IllegalArgumentException("student is already in this class")
        } else {
            if (student.studentClass != this) {
                throw IllegalArgumentException("student is not in this class")
            } else {
                students = students + student
            }
        }
    }

    /**
     * Adds a professor to the class.
     * If the professor is already in the class, the subjects taught by the professor in the class are updated.
     *
     * @param professor the professor to add
     * @param subjects the subjects taught by the professor in the class
     * @throws IllegalArgumentException if the professor is not in the class or if the professor doesn't teach the subjects in the class
     */
    override fun addProfessor(professor: Professor, subjects: Set<Subject>) {
        if (!professor.professorClasses.contains(this)) {
            throw IllegalArgumentException("professor is not in this class")
        } else {
            if (professor.professorSubjectsInClasses[this]?.containsAll(subjects) == false) {
                throw IllegalArgumentException("professor doesn't teach this subjects in this class")
            } else {
                if (professors.contains(professor)) {
                    val oldSubjects = professorTeachSubjects[professor]!!
                    professorTeachSubjects = professorTeachSubjects + (professor to oldSubjects + subjects)
                } else {
                    professors = professors + professor
                    professorTeachSubjects = professorTeachSubjects + (professor to subjects)
                }
            }
        }
    }
}
