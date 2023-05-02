package com.intelligentbackpack.accessdomain.usecase

import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.accessdomain.repository.AccessDomainRepository

class AccessUseCase(private val repository: AccessDomainRepository) {

    suspend fun createUser(user: User) = repository.createUser(user)

    suspend fun isUserLogged() = repository.isUserLogged()

    suspend fun loginWithData(username: String, password: String) = repository.loginWithData(username, password)

    suspend fun automaticLogin() = repository.automaticLogin()

    suspend fun logoutUser() = repository.logoutUser()
}
