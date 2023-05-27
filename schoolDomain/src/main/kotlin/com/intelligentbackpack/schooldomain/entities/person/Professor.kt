package com.intelligentbackpack.schooldomain.entities.person

import com.intelligentbackpack.schooldomain.entities.Subject
import com.intelligentbackpack.schooldomain.entities.Class

/**
 * A professor.
 *
 * @property name the name of the professor
 * @property surname the surname of the professor
 * @property email the email of the professor
 * @property professorClasses the classes the professor teaches
 * @property professorSubjects the subjects the professor teaches
 * @property professorSubjectsInClasses the subjects the professor teaches in each class
 */
interface Professor : Person {
    val subjects: Set<Subject>
    val professorSubjectsInClasses: Map<Class, Set<Subject>>
    val professorClasses: Set<Class>
        get() = professorSubjectsInClasses.keys

    val professorSubjects: Set<Subject>
        get() = professorSubjectsInClasses.values.flatten().toSet()

    /**
     * Adds the professor to a class.
     * If the professor is already in the class, the subjects are added to the subjects the professor teaches in the class.
     *
     * @param professorClass the class to add the professor to
     * @param subjects the subjects the professor teaches in the class
     * @throws IllegalArgumentException if the subjects are empty
     */
    fun addProfessorToClass(professorClass: Class, subjects: Set<Subject>)

    companion object {

        private data class ProfessorImpl(
            override val email: String,
            override val name: String,
            override val surname: String,
        ) : Professor {
            override val subjects: Set<Subject>
                get() = professorSubjectsInClasses.values.flatten().toSet()
            override var professorSubjectsInClasses: Map<Class, Set<Subject>> = mapOf()
                private set

            override fun addProfessorToClass(professorClass: Class, subjects: Set<Subject>) {
                if (subjects.isEmpty()) {
                    throw IllegalArgumentException("subjects cannot be empty")
                } else {
                    professorSubjectsInClasses = if (professorSubjectsInClasses.containsKey(professorClass)) {
                        val oldSubjects = professorSubjectsInClasses[professorClass]!!
                        professorSubjectsInClasses + (professorClass to oldSubjects + subjects)
                    } else {
                        professorSubjectsInClasses + (professorClass to subjects)
                    }
                }
            }
        }

        /**
         * Creates a professor.
         *
         * @param email the email of the professor
         * @param name the name of the professor
         * @param surname the surname of the professor
         * @param professorClasses the classes the professor teaches
         * @return the created professor
         * @throws IllegalArgumentException if the email is blank
         * @throws IllegalArgumentException if the name is blank
         * @throws IllegalArgumentException if the surname is blank
         * @throws IllegalArgumentException if the professorClasses are empty
         */
        fun create(
            email: String,
            name: String,
            surname: String,
            professorClasses: Map<Class, Set<Subject>>,
        ): Professor {
            if (email.isBlank()) {
                throw IllegalArgumentException("email cannot be blank")
            } else if (name.isBlank()) {
                throw IllegalArgumentException("name cannot be blank")
            } else if (surname.isBlank()) {
                throw IllegalArgumentException("surname cannot be blank")
            } else {
                if (professorClasses.any { it.value.isEmpty() }) {
                    throw IllegalArgumentException("professorClasses cannot be empty")
                } else {
                    return ProfessorImpl(email, name, surname).apply {
                        professorClasses.forEach { addProfessorToClass(it.key, it.value) }
                    }
                }
            }
        }
    }
}
