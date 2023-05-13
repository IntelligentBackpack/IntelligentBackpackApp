package com.intelligentbackpack.accessdomain.usecase

import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.accessdomain.exceptions.UserAlreadyLogged
import com.intelligentbackpack.accessdomain.repository.AccessDomainRepository

/**
 * AccessUseCase is the use case for the access domain.
 * @param repository is the repository to use.
 */
class AccessUseCase(private val repository: AccessDomainRepository) {

    private var user: User? = null

    /**
     * Creates a new user.
     * @param user is the user to create.
     * @param success is the success callback.
     * @param error is the error callback.
     */
    suspend fun createUser(user: User, success: (User) -> Unit, error: (Exception) -> Unit) =
        if (this.user != null)
            error(UserAlreadyLogged(user.email))
        else {
            repository.createUser(user = user, error = error, success = {
                this.user = it
                success(it)
            })
        }

    /**
     * Checks if a user is logged.
     */
    val isUserLogged
        get() = user != null

    /**
     * Logs a user using email and password.
     *
     * @param email is the user email.
     * @param password is the user password.
     * @param success is the success callback with the logged user.
     * @param error is the error callback.
     */
    suspend fun loginWithData(email: String, password: String, success: (User) -> Unit, error: (Exception) -> Unit) =
        repository.loginWithData(email, password, error = error, success = {
            user = it
            success(it)
        })

    /**
     * Logs the saved user.
     *
     * @param success is the success callback with the logged user.
     * @param error is the error callback.
     */
    suspend fun automaticLogin(success: (User) -> Unit, error: (Exception) -> Unit) =
        user?.let { success(it) }
            ?: repository.automaticLogin(error = error, success = {
                user = it
                success(it)
            })

    /**
     * Logs out the user.
     *
     * @param success is the success callback.
     * @param error is the error callback.
     */
    suspend fun logoutUser(success: (User) -> Unit, error: (Exception) -> Unit) =
        repository.logoutUser(error = error, success = {
            user = null
            success(it)
        })

    /**
     * Deletes the user.
     *
     * @param success is the success callback.
     * @param error is the error callback.
     */
    suspend fun deleteUser(success: (User) -> Unit, error: (Exception) -> Unit) = repository.deleteUser({
        user = null
        success(it)
    }, error)
}
