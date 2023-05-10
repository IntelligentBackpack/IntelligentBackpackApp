package com.intelligentbackpack.accessdomain.usecase

import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.accessdomain.repository.AccessDomainRepository

/**
 * AccessUseCase is the use case for the access domain.
 * @param repository is the repository to use.
 */
class AccessUseCase(private val repository: AccessDomainRepository) {

    /**
     * Creates a new user.
     * @param user is the user to create.
     * @param success is the success callback.
     * @param error is the error callback.
     */
    suspend fun createUser(user: User, success: (User) -> Unit, error: (Exception) -> Unit) =
        repository.createUser(user, success, error)

    /**
     * Checks if a user is logged.
     *
     * @param success is the success callback with true if the user is logged, false otherwise.
     * @param error is the error callback.
     */
    suspend fun isUserLogged(success: (Boolean) -> Unit, error: (Exception) -> Unit) =
        repository.isUserLogged(success, error)

    /**
     * Logs a user using email and password.
     *
     * @param email is the user email.
     * @param password is the user password.
     * @param success is the success callback with the logged user.
     * @param error is the error callback.
     */
    suspend fun loginWithData(email: String, password: String, success: (User) -> Unit, error: (Exception) -> Unit) =
        repository.loginWithData(email, password, success, error)

    /**
     * Logs the saved user.
     *
     * @param success is the success callback with the logged user.
     * @param error is the error callback.
     */
    suspend fun automaticLogin(success: (User) -> Unit, error: (Exception) -> Unit) =
        repository.automaticLogin(success, error)

    /**
     * Logs out the user.
     *
     * @param success is the success callback.
     * @param error is the error callback.
     */
    suspend fun logoutUser(success: (User) -> Unit, error: (Exception) -> Unit) = repository.logoutUser(success, error)

    /**
     * Deletes the user.
     *
     * @param success is the success callback.
     * @param error is the error callback.
     */
    suspend fun deleteUser(success: (User) -> Unit, error: (Exception) -> Unit) = repository.deleteUser(success, error)
}
