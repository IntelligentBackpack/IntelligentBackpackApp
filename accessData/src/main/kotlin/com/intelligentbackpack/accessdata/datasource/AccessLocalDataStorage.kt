package com.intelligentbackpack.accessdata.datasource

import com.intelligentbackpack.accessdomain.entities.User

interface AccessLocalDataStorage {
    fun isUserSaved(): Boolean
    fun saveUser(user: User)
    fun getUser(): User
    fun deleteUser()
}