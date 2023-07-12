package com.intelligentbackpack.accessdomain.entities

import com.intelligentbackpack.accessdomain.exceptions.InvalidEmailException
import com.intelligentbackpack.accessdomain.exceptions.InvalidPasswordException
import com.intelligentbackpack.accessdomain.policies.EmailFormatPolicy
import com.intelligentbackpack.accessdomain.policies.PasswordFormatPolicy
import com.intelligentbackpack.accessdomain.policies.Policy
import com.intelligentbackpack.accessdomain.policies.StringFormat.firstLetterToUpperCaseWithNoSpace

/**
 * User role is used to define the user's permissions.
 */
enum class Role {
    /**
     * User role is the default role.
     */
    USER,

    /**
     * professor role is used to define the professor's permissions.
     */
    PROFESSOR,

    /**
     * Student role is used to define the student's permissions.
     */
    STUDENT,
}

/**
 * Type-alias for email.
 */
typealias Email = String

/**
 * Type-alias for password.
 */
typealias Password = String

/**
 * User interface.
 */
interface User {
    /**
     * User email.
     */
    val email: Email

    /**
     * User name.
     */
    val name: String

    /**
     * User surname.
     */
    val surname: String

    /**
     * User password.
     */
    val password: Password

    /**
     * User role.
     */
    val role: Role

    companion object {
        /**
         * User builder.
         */
        inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
    }

    /**
     * User builder.
     */
    class Builder {
        /**
         * User email in builder.
         *
         * Must be a valid email (see [EmailFormatPolicy]).
         */
        lateinit var email: Email

        /**
         * User name in builder.
         */
        var name: String = ""

        /**
         * User surname in builder.
         */
        var surname: String = ""

        /**
         * User password in builder.
         *
         * Must be a valid password (see [PasswordFormatPolicy]).
         */
        lateinit var password: Password

        /**
         * User role in builder.
         */
        var role: Role = Role.USER
        private val emailPolicies: List<Policy<Email>> = listOf(EmailFormatPolicy)
        private val passwordPolicies: List<Policy<Password>> = listOf(PasswordFormatPolicy)

        /**
         * Build the user.
         *
         * @throws IllegalArgumentException if the email, name, surname or password are not valid (blank or doesn't respect the policies).
         */
        fun build(): User =
            with(this) {
                val trimEmail = email.trim()
                if (emailPolicies.all { it.isRespected(trimEmail) }) {
                    if (passwordPolicies.all { it.isRespected(password) }) {
                        require(name.isNotBlank()) { "Name cannot be blank." }
                        require(surname.isNotBlank()) { "Surname cannot be blank." }
                        UserImpl(
                            email = trimEmail,
                            name = name.firstLetterToUpperCaseWithNoSpace(),
                            surname = surname.firstLetterToUpperCaseWithNoSpace(),
                            password = password,
                            role = role,
                        )
                    } else {
                        throw InvalidPasswordException()
                    }
                } else {
                    throw InvalidEmailException()
                }
            }
    }
}
