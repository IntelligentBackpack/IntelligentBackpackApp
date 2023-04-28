package com.intelligentbackpack.accessdata.datasource

import com.intelligentbackpack.accessdomain.entities.User

interface AccessRemoteDataStorage {

    fun createUser(user: User): User

    fun accessWithData(username: String, password: String) : User
}