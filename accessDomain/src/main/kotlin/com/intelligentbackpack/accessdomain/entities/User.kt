package com.intelligentbackpack.accessdomain.entities

import com.intelligentbackpack.accessdomain.policies.EmailFormatPolicy
import com.intelligentbackpack.accessdomain.policies.PasswordFormatPolicy
import com.intelligentbackpack.accessdomain.policies.Policy
import java.util.Locale

enum class Role {
    USER, TEACHER, STUDENT
}

typealias Email = String
typealias Password = String

interface User {

    val email: Email
    val name: String
    val surname: String
    val password: Password
    val role: Role

    companion object {
        inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
    }

    class Builder {
        lateinit var email: Email
        var name: String = ""
        var surname: String = ""
        lateinit var password: Password
        var role: Role = Role.USER
        private val emailPolicies: List<Policy<Email>> = listOf(EmailFormatPolicy)
        private val passwordPolicies: List<Policy<Password>> = listOf(PasswordFormatPolicy)

        fun build(): User =
            with(this) {
                val trimEmail = email.trim()
                if (emailPolicies.all { it.isRespected(trimEmail) })
                    if (name.isNotBlank())
                        if (surname.isNotBlank())
                            if (passwordPolicies.all { it.isRespected(password) })
                                UserImpl(
                                    email = trimEmail,
                                    name = name
                                        .trim()
                                        .replaceFirstChar {
                                            if (it.isLowerCase())
                                                it.titlecase(Locale.ROOT)
                                            else
                                                it.toString()
                                        },
                                    surname = surname
                                        .trim()
                                        .replaceFirstChar {
                                            if (it.isLowerCase())
                                                it.titlecase(Locale.ROOT)
                                            else it.toString()
                                        },
                                    password = password,
                                    role = role
                                )
                            else
                                throw IllegalArgumentException("Password not valid")
                        else
                            throw IllegalArgumentException("Surname not valid")
                    else
                        throw IllegalArgumentException("Name not valid")
                else
                    throw IllegalArgumentException("Email not valid")
            }
    }
}
