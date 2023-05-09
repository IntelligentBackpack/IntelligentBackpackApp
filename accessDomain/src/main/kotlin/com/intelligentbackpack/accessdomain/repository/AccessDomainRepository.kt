package com.intelligentbackpack.accessdomain.repository

import com.intelligentbackpack.accessdomain.entities.User

/**
 * AccessDomainRepository is the repository interface for the access domain.
 */
interface AccessDomainRepository {

    /**
     * Creates a new user.
     * @param user is the user to create.
     * @return the created user.
     */
    fun createUser(user: User): User

    /**
     * Checks if a user is logged.
     * @return true if the user is logged, false otherwise.
     */
    fun isUserLogged(): Boolean

    /**
     * Logs a user using email and password.
     * @param email is the user email.
     * @param password is the user password.
     * @return the logged user.
     */
    fun loginWithData(email: String, password: String): User

    /**
     * Logs the saved user.
     * @return the logged user.
     */
    fun automaticLogin(): User

    /**
     * Logs out the user.
     */
    fun logoutUser()

    /**
     * Deletes the user.
     */
    fun deleteUser()
}
