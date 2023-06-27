package com.intelligentbackpack.accessdomain.policies

import com.intelligentbackpack.accessdomain.entities.Email

/**
 * EmailFormatPolicy is a policy that checks if the email respects the format.
 *
 * The email must contain at least:
 * - one @
 * - one .
 * - 2 characters after the last .
 * - no spaces
 * - no , or @ before the last .
 */
object EmailFormatPolicy : Policy<Email> {
    private val regex = Regex("^[^\\s@]+@([^\\s@.,]+\\.)+[^\\s@.,]{2,}\$")

    override fun isRespected(entity: Email): Boolean {
        return regex.matches(entity)
    }
}
