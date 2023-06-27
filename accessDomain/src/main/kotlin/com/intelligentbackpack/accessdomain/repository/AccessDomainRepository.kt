package com.intelligentbackpack.accessdomain.repository

import com.intelligentbackpack.accessdomain.entities.User

/**
 * AccessDomainRepository is the repository interface for the access domain.
 */
interface AccessDomainRepository {

    /**
     * Checks if a user is logged.
     *
     * @return true if a user is logged, false otherwise.
     */
    suspend fun isUserLogged(): Boolean

    /**
     * Creates a new user.
     *
     * @param user is the user to create.
     * @return the created user.
     */
    suspend fun createUser(user: User): User

    /**
     * Logs a user using email and password.
     *
     * @param email is the user email.
     * @param password is the user password.
     * @return the logged user.
     */
    suspend fun loginWithData(email: String, password: String): User

    /**
     * Logs the saved user.
     *
     * @return the logged user.
     */
    suspend fun automaticLogin(): User

    /**
     * Logs out the user.
     *
     * @return the logged out user.
     */
    suspend fun logoutUser(): User

    /**
     * Deletes the user.
     *
     * @return the deleted user.
     */
    suspend fun deleteUser(): User

    /**
     * Gets the logged user.
     *
     * @return the logged user.
     */
    suspend fun getLoggedUser(): User
}
