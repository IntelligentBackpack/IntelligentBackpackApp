package com.intelligentbackpack.accessdata.datasource

import com.intelligentbackpack.accessdomain.entities.User

/**
 * AccessRemoteDataStorage is the interface for access to the remote data storage.
 */
interface AccessRemoteDataSource {

    /**
     * Creates a user.
     * @param user is the user to create.
     * @return the created user.
     */
    fun createUser(user: User): User

    /**
     * Logs a user using email and password.
     * @param email is the user email.
     * @param password is the user password.
     * @return the logged user.
     */
    fun accessWithData(email: String, password: String): User

    /**
     * Logs the saved user.
     * @return the logged user.
     */
    fun deleteUser(user: User)
}