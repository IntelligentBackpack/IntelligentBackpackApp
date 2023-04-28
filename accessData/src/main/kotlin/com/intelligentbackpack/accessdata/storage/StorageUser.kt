package com.intelligentbackpack.accessdata.storage

import com.intelligentbackpack.accessdomain.entities.User

interface StorageUser {

    fun isUserSaved(): Boolean

    fun saveUser(user: User)

    fun getUser(): User

    fun deleteUser()


}