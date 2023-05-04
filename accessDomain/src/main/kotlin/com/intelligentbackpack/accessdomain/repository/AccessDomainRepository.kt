package com.intelligentbackpack.accessdomain.repository

import com.intelligentbackpack.accessdomain.entities.User

interface AccessDomainRepository {

    fun createUser(user: User): User

    fun isUserLogged(): Boolean

    fun loginWithData(email: String, password: String): User

    fun automaticLogin(): User

    fun logoutUser()

    fun deleteUser()
}
