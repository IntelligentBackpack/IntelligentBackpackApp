package com.intelligentbackpack.accessdata.datasource

import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.accessdomain.repository.AccessDomainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * AccessDomainRepositoryImpl is the implementation of the AccessDomainRepository.
 * @param accessLocalDataSource is the local data storage.
 * @param accessRemoteDataSource is the remote data storage.
 */
class AccessDomainRepositoryImpl(
    private val accessLocalDataSource: AccessLocalDataSource,
    private val accessRemoteDataSource: AccessRemoteDataSource
) : AccessDomainRepository {

    /**
     * Checks if a user is logged.
     *
     * @return true if a user is logged, false otherwise.
     */
    override suspend fun isUserLogged(): Boolean =
        withContext(Dispatchers.IO) {
            accessLocalDataSource.isUserSaved()
        }

    /**
     * Creates a new user.
     *
     * @param user is the user to create.
     *
     * The user is created in the remote data storage and then saved in the local data storage.
     */
    override suspend fun createUser(user: User): User =
        withContext(Dispatchers.IO) {
            accessRemoteDataSource.createUser(user)
                .also { accessLocalDataSource.saveUser(it) }
        }

    /**
     * Logs a user using email and password.
     *
     * @param email is the user email.
     * @param password is the user password.
     * @return the logged user.
     *
     * The user is logged in the remote data storage and then saved in the local data storage.
     */
    override suspend fun loginWithData(email: String, password: String): User =
        withContext(Dispatchers.IO) {
            accessRemoteDataSource.accessWithData(email, password)
                .also { accessLocalDataSource.saveUser(it) }
        }


    /**
     * Logs the saved user.
     *
     * The user is logged in the remote data storage.
     */
    override suspend fun automaticLogin(): User =
        withContext(Dispatchers.IO) {
            val user = accessLocalDataSource.getUser()
            accessRemoteDataSource.accessWithData(user.email, user.password).also {
                accessLocalDataSource.saveUser(it)
            }
        }

    /**
     * Logs out the user.
     *
     * @return the logged out user.
     *
     * The user is deleted in the local data storage.
     */
    override suspend fun logoutUser(): User =
        withContext(Dispatchers.IO) {
            accessLocalDataSource.getUser()
                .also { accessLocalDataSource.deleteUser() }
        }


    /**
     * Deletes the user.
     *
     * @return the deleted user.
     *
     * The user is deleted in the remote data storage and then in the local data storage.
     */
    override suspend fun deleteUser(): User =
        withContext(Dispatchers.IO) {
            accessLocalDataSource.getUser()
                .also { accessRemoteDataSource.deleteUser(it) }
                .also { accessLocalDataSource.deleteUser() }
        }
}
