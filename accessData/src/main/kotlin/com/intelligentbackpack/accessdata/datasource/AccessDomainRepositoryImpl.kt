package com.intelligentbackpack.accessdata.datasource

import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.accessdomain.repository.AccessDomainRepository

/**
 * AccessDomainRepositoryImpl is the implementation of the AccessDomainRepository.
 * @param accessLocalDataStorage is the local data storage.
 * @param accessRemoteDataStorage is the remote data storage.
 */
class AccessDomainRepositoryImpl(
    private val accessLocalDataStorage: AccessLocalDataStorage,
    private val accessRemoteDataStorage: AccessRemoteDataStorage
) : AccessDomainRepository {

    /**
     * Creates a new user.
     * @param user is the user to create.
     * @return the created user.
     * @throws Exception if the user cannot be created.
     *
     * The user is created in the remote data storage and then saved in the local data storage.
     */
    override fun createUser(user: User): User =
        accessRemoteDataStorage.createUser(user)
            .also { accessLocalDataStorage.saveUser(it) }

    /**
     * Checks if a user is logged.
     * @return true if the user is logged, false otherwise.
     *
     * The user is checked in the local data storage.
     */
    override fun isUserLogged(): Boolean = accessLocalDataStorage.isUserSaved()

    /**
     * Logs a user using email and password.
     * @param email is the user email.
     * @param password is the user password.
     * @return the logged user.
     * @throws Exception if the user cannot be logged.
     *
     * The user is logged in the remote data storage and then saved in the local data storage.
     */
    override fun loginWithData(email: String, password: String): User =
        accessRemoteDataStorage
            .accessWithData(email, password)
            .also { accessLocalDataStorage.saveUser(it) }

    /**
     * Logs the saved user.
     * @return the logged user.
     * @throws Exception if the user cannot be logged.
     *
     * The user is logged in the remote data storage.
     */
    override fun automaticLogin(): User =
        accessLocalDataStorage.getUser()

    /**
     * Logs out the user.
     * @throws Exception if the user cannot be logged out.
     *
     * The user is deleted in the local data storage.
     */
    override fun logoutUser() =
        accessLocalDataStorage.deleteUser()

    /**
     * Deletes the user.
     * @throws Exception if the user cannot be deleted.
     *
     * The user is deleted in the remote data storage and then in the local data storage.
     */
    override fun deleteUser() {
        accessRemoteDataStorage.deleteUser(accessLocalDataStorage.getUser())
        accessLocalDataStorage.deleteUser()
    }

}