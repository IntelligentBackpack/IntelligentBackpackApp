package com.intelligentbackpack.accessdomain.policies

/**
 * PasswordFormatPolicy is a policy that checks if the password respects the format.
 *
 * The password must contain at least:
 * - one uppercase letter
 * - one lowercase letter
 * - one digit
 * - one special character
 * - 8 characters
 */
object PasswordFormatPolicy : Policy<String> {
    private val regex = Regex("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")

    override fun isRespected(entity: String): Boolean {
        return regex.matches(entity)
    }
}
