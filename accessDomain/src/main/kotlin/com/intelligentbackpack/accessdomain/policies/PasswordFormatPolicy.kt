package com.intelligentbackpack.accessdomain.policies

object PasswordFormatPolicy : Policy<String> {
    private val regex = Regex("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")

    override fun isRespected(entity: String): Boolean {
        return regex.matches(entity)
    }
}
