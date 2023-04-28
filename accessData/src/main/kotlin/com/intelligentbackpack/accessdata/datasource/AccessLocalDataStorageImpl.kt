package com.intelligentbackpack.accessdata.datasource

import com.intelligentbackpack.accessdata.exception.MissingUserException
import com.intelligentbackpack.accessdata.exception.SavingUserException
import com.intelligentbackpack.accessdata.storage.StorageUser
import com.intelligentbackpack.accessdomain.entities.User

class AccessLocalDataStorageImpl(private val storage: StorageUser) : AccessLocalDataStorage {
    override fun isUserSaved(): Boolean {
        return storage.isUserSaved()
    }

    override fun saveUser(user: User) {
        try {
            storage.saveUser(user)
        }
        catch (e: Exception) {
            throw SavingUserException()
        }
    }

    override fun getUser(): User {
        if (!storage.isUserSaved())
            throw MissingUserException()
        return storage.getUser()
    }

    override fun deleteUser() {
        storage.deleteUser()
    }
}