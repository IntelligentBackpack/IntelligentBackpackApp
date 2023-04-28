package com.intelligentbackpack.accessdomain.policies

import com.intelligentbackpack.accessdomain.entities.Email

object EmailFormatPolicy : Policy<Email> {
    private val regex = Regex("^[^\\s@]+@([^\\s@.,]+\\.)+[^\\s@.,]{2,}\$")

    override fun isRespected(entity: Email): Boolean {
        return regex.matches(entity)
    }
}
