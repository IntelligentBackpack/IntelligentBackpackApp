package com.intelligentbackpack.schooldomain.entities.person

/**
 * A professor.
 *
 * @property name the name of the professor
 * @property surname the surname of the professor
 * @property email the email of the professor
 */
interface Professor : Person {

    companion object {

        private data class ProfessorImpl(
            override val email: String,
            override val name: String,
            override val surname: String,
        ) : Professor

        /**
         * Creates a professor.
         *
         * @param email the email of the professor
         * @param name the name of the professor
         * @param surname the surname of the professor
         * @return the created professor
         * @throws IllegalArgumentException if the email is blank
         * @throws IllegalArgumentException if the name is blank
         * @throws IllegalArgumentException if the surname is blank
         */
        fun create(
            email: String,
            name: String,
            surname: String,
        ): Professor {
            require(email.isNotBlank()) { "email cannot be blank" }
            require(name.isNotBlank()) { "name cannot be blank" }
            require(surname.isNotBlank()) { "surname cannot be blank" }
            return ProfessorImpl(email, name, surname)
        }
    }
}
