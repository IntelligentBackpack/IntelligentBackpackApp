package com.intelligentbackpack.accessdomain.usecase

import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.accessdomain.repository.AccessDomainRepository

/**
 * AccessUseCase is the use case for the access domain.
 * @param repository is the repository to use.
 */
class AccessUseCase(private val repository: AccessDomainRepository) {

    /**
     * Creates a new user.
     * @param user is the user to create.
     * @return the created user.
     */
    suspend fun createUser(user: User) = repository.createUser(user)

    /**
     * Checks if a user is logged.
     * @return true if the user is logged, false otherwise.
     */
    suspend fun isUserLogged() = repository.isUserLogged()

    /**
     * Logs a user using email and password.
     * @param email is the user email.
     * @param password is the user password.
     * @return the logged user.
     */
    suspend fun loginWithData(email: String, password: String) = repository.loginWithData(email, password)

    /**
     * Logs the saved user.
     * @return the logged user.
     */
    suspend fun automaticLogin() = repository.automaticLogin()

    /**
     * Logs out the user.
     */
    suspend fun logoutUser() = repository.logoutUser()

    /**
     * Deletes the user.
     */
    suspend fun deleteUser() = repository.deleteUser()
}
