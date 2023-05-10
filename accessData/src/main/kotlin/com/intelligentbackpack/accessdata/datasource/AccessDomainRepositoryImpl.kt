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
     *
     * @param user is the user to create.
     * @param success is the success callback.
     * @param error is the error callback.
     *
     * The user is created in the remote data storage and then saved in the local data storage.
     */
    override fun createUser(user: User, success: (User) -> Unit, error: (Exception) -> Unit) {
        try {
            success(
                accessRemoteDataStorage.createUser(user)
                    .also { accessLocalDataStorage.saveUser(it) }
            )
        } catch (e: Exception) {
            error(e)
        }
    }


    /**
     * Checks if a user is logged.
     *
     * @param success is the success callback with true if the user is logged, false otherwise.
     * @param error is the error callback.
     *
     * The user is checked in the local data storage.
     */
    override fun isUserLogged(success: (Boolean) -> Unit, error: (Exception) -> Unit) {
        try {
            success(accessLocalDataStorage.isUserSaved())
        } catch (e: Exception) {
            error(e)
        }
    }

    /**
     * Logs a user using email and password.
     *
     * @param email is the user email.
     * @param password is the user password.
     * @param success is the success callback with the logged user.
     * @param error is the error callback.
     *
     * The user is logged in the remote data storage and then saved in the local data storage.
     */
    override fun loginWithData(email: String, password: String, success: (User) -> Unit, error: (Exception) -> Unit) {
        try {
            success(
                accessRemoteDataStorage.accessWithData(email, password)
                    .also { accessLocalDataStorage.saveUser(it) }
            )
        } catch (e: Exception) {
            error(e)
        }
    }

    /**
     * Logs the saved user.
     *
     * @param success is the success callback with the logged user.
     * @param error is the error callback.
     *
     * The user is logged in the remote data storage.
     */
    override fun automaticLogin(success: (User) -> Unit, error: (Exception) -> Unit) {
        try {
            success(accessLocalDataStorage.getUser())
        } catch (e: Exception) {
            error(e)
        }
    }

    /**
     * Logs out the user.
     *
     * @param success is the success callback with the logged out user.
     * @param error is the error callback.
     *
     * The user is deleted in the local data storage.
     */
    override fun logoutUser(success: (User) -> Unit, error: (Exception) -> Unit) {
        try {
            success(accessLocalDataStorage.getUser())
        } catch (e: Exception) {
            error(e)
        }
    }

    /**
     * Deletes the user.
     *
     * @param success is the success callback with the deleted user.
     * @param error is the error callback.
     *
     * The user is deleted in the remote data storage and then in the local data storage.
     */
    override fun deleteUser(success: (User) -> Unit, error: (Exception) -> Unit) {
        try {
            val user = accessLocalDataStorage.getUser()
            accessRemoteDataStorage.deleteUser(user)
            accessLocalDataStorage.deleteUser()
            success(user)
        } catch (e: Exception) {
            error(e)
        }
    }
}
