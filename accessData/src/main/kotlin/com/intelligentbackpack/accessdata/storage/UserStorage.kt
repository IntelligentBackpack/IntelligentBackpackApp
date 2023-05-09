package com.intelligentbackpack.accessdata.storage

import com.intelligentbackpack.accessdomain.entities.User

/**
 * UserStorage is the interface for storage the user data.
 */
interface UserStorage {

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