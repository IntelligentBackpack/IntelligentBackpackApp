package com.intelligentbackpack.accessdomain.repository

import com.intelligentbackpack.accessdomain.entities.User

/**
 * AccessDomainRepository is the repository interface for the access domain.
 */
interface AccessDomainRepository {

    /**
     * Creates a new user.
     *
     * @param user is the user to create.
     * @param success is the success callback.
     * @param error is the error callback.
     */
    suspend fun createUser(user: User, success: (User) -> Unit, error: (Exception) -> Unit)

    /**
     * Logs a user using email and password.
     *
     * @param email is the user email.
     * @param password is the user password.
     * @param success is the success callback.
     * @param error is the error callback.
     */
    suspend fun loginWithData(email: String, password: String, success: (User) -> Unit, error: (Exception) -> Unit)

    /**
     * Logs the saved user.
     *
     * @param success is the success callback.
     * @param error is the error callback.
     */
    suspend fun automaticLogin(success: (User) -> Unit, error: (Exception) -> Unit)

    /**
     * Logs out the user.
     *
     * @param success is the success callback.
     * @param error is the error callback.
     */
    suspend fun logoutUser(success: (User) -> Unit, error: (Exception) -> Unit)

    /**
     * Deletes the user.
     *
     * @param success is the success callback.
     * @param error is the error callback.
     */
    suspend fun deleteUser(success: (User) -> Unit, error: (Exception) -> Unit)
}
