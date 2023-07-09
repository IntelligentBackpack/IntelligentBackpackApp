package com.intelligentbackpack.schooldomain.entities.person

/**
 * A student.
 *
 * @property email the email of the student
 * @property name the name of the student
 * @property surname the surname of the student
 */
interface Student : Person {

    companion object {
        private data class StudentImpl(
            override val email: String,
            override val name: String,
            override val surname: String,
        ) : Student

        /**
         * Creates a student.
         *
         * @param email the email of the student
         * @param name the name of the student
         * @param surname the surname of the student
         * @throws IllegalArgumentException if the email or name or surname is blank
         *
         */
        fun create(
            email: String,
            name: String,
            surname: String,
        ): Student {
            check(email.isBlank()) { "email cannot be blank" }
            check(name.isBlank()) { "name cannot be blank" }
            check(surname.isBlank()) { "surname cannot be blank" }
            return StudentImpl(email, name, surname)
        }
    }
}
