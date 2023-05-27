package com.intelligentbackpack.schooldomain.entities.person

import com.intelligentbackpack.schooldomain.entities.Class

/**
 * A student.
 *
 * @property email the email of the student
 * @property name the name of the student
 * @property surname the surname of the student
 * @property studentClass the class the student attends
 */
interface Student : Person {
    val studentClass: Class

    companion object {
        private data class StudentImpl(
            override val email: String,
            override val name: String,
            override val surname: String,
            override val studentClass: Class,
        ) : Student

        /**
         * Creates a student.
         *
         * @param email the email of the student
         * @param name the name of the student
         * @param surname the surname of the student
         * @param studentClass the class the student attends
         * @throws IllegalArgumentException if the email or name or surname is blank
         *
         */
        fun create(
            email: String,
            name: String,
            surname: String,
            studentClass: Class,
        ): Student {
            if (email.isBlank()) {
                throw IllegalArgumentException("email cannot be blank")
            } else if (name.isBlank()) {
                throw IllegalArgumentException("name cannot be blank")
            } else if (surname.isBlank()) {
                throw IllegalArgumentException("surname cannot be blank")
            } else {
                return StudentImpl(email, name, surname, studentClass)
            }
        }
    }
}
