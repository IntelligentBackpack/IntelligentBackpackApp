package com.intelligentbackpack.accessdata.datasource

import com.intelligentbackpack.accessdomain.entities.User

interface AccessRemoteDataStorage {

    fun createUser(user: User): User

    fun accessWithData(email: String, password: String) : User

    fun deleteUser(user: User)
}