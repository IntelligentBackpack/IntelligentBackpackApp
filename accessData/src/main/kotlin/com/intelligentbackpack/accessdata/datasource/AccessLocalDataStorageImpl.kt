package com.intelligentbackpack.accessdata.datasource

import com.intelligentbackpack.accessdata.exception.MissingUserException
import com.intelligentbackpack.accessdata.exception.SavingUserException
import com.intelligentbackpack.accessdata.storage.UserStorage
import com.intelligentbackpack.accessdomain.entities.User

/**
 * AccessLocalDataStorageImpl is the implementation of AccessLocalDataStorage.
 * @param storage is the storage of the user data [UserStorage].
 */
class AccessLocalDataStorageImpl(private val storage: UserStorage) : AccessLocalDataStorage {

    override fun isUserSaved(): Boolean {
        return storage.isUserSaved()
    }

    /**
     * Saves the user.
     * @param user is the user to save.
     * @throws SavingUserException if the user cannot be saved.
     */
    @Throws(SavingUserException::class)
    override fun saveUser(user: User) {
        try {
            storage.saveUser(user)
        } catch (e: Exception) {
            throw SavingUserException()
        }
    }

    /**
     * Gets the user.
     * @return the user.
     * @throws MissingUserException if the user is not saved.
     */
    @Throws(MissingUserException::class)
    override fun getUser(): User {
        if (!storage.isUserSaved())
            throw MissingUserException()
        return storage.getUser()
    }

    override fun deleteUser() {
        storage.deleteUser()
    }
}