package com.intelligentbackpack.accessdata.datasource

import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.accessdomain.repository.AccessDomainRepository

class AccessDomainRepositoryImpl(
    private val accessLocalDataStorage: AccessLocalDataStorage,
    private val accessRemoteDataStorage: AccessRemoteDataStorage
) : AccessDomainRepository {

    override fun createUser(user: User): User =
        accessRemoteDataStorage.createUser(user)
            .also { accessLocalDataStorage.saveUser(it) }

    override fun isUserLogged(): Boolean = accessLocalDataStorage.isUserSaved()

    override fun loginWithData(email: String, password: String): User =
        accessRemoteDataStorage
            .accessWithData(email, password)
            .also { accessLocalDataStorage.saveUser(it) }

    override fun automaticLogin(): User =
        accessLocalDataStorage.getUser()


    override fun logoutUser() =
        accessLocalDataStorage.deleteUser()

}