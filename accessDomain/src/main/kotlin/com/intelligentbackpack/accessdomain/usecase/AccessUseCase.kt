package com.intelligentbackpack.accessdomain.usecase

import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.accessdomain.exceptions.UserAlreadyLogged
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
        if (!repository.isUserLogged()) {
            try {
                success(repository.createUser(user = user))
            } catch (e: Exception) {
                error(e)
            }
        } else {
            error(UserAlreadyLogged(user))
        }

    /**
     * Logs a user using email and password.
     *
     * @param email is the user email.
     * @param password is the user password.
     * @param success is the success callback with the logged user.
     * @param error is the error callback.
     */
    suspend fun loginWithData(
        email: String,
        password: String,
        success: (User) -> Unit,
        error: (Exception) -> Unit
    ) =
        try {
            success(repository.loginWithData(email, password))
        } catch (e: Exception) {
            error(e)
        }

    /**
     * Logs the saved user.
     *
     * @param success is the success callback with the logged user.
     * @param error is the error callback.
     */
    suspend fun automaticLogin(success: suspend (User) -> Unit, error: (Exception) -> Unit) =
        try {
            success(repository.automaticLogin())
        } catch (e: Exception) {
            error(e)
        }

    /**
     * Logs out the user.
     *
     * @param success is the success callback.
     * @param error is the error callback.
     */
    suspend fun logoutUser(success: (User) -> Unit, error: (Exception) -> Unit) =
        try {
            success(repository.logoutUser())
        } catch (e: Exception) {
            error(e)
        }

    /**
     * Deletes the user.
     *
     * @param success is the success callback.
     * @param error is the error callback.
     */
    suspend fun deleteUser(success: (User) -> Unit, error: (Exception) -> Unit) =
        try {
            success(repository.deleteUser())
        } catch (e: Exception) {
            error(e)
        }
}
