package com.intelligentbackpack.accessdata.datasource

import com.intelligentbackpack.accessdomain.entities.User

/**
 * AccessLocalDataStorage is the interface for access to the local data storage.
 */
interface AccessLocalDataSource {

    /**
     * Checks if the user is saved.
     * @return true if the user is saved, false otherwise.
     */
    fun isUserSaved(): Boolean

    /**
     * Saves the user.
     * @param user is the user to save.
     */
    fun saveUser(user: User)

    /**
     * Gets the user.
     * @return the user.
     */
    fun getUser(): User

    /**
     * Deletes the user.
     */
    fun deleteUser()
}