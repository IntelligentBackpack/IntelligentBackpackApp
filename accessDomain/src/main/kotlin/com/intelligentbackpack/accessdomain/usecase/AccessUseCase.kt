package com.intelligentbackpack.accessdomain.usecase

import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.accessdomain.exceptions.UserAlreadyLoggedException
import com.intelligentbackpack.accessdomain.repository.AccessDomainRepository

/**
 * AccessUseCase is the use case for the access domain.
 * @param repository is the repository to use.
 */
class AccessUseCase(private val repository: AccessDomainRepository) {

    var onUserLogin: (suspend (User) -> Unit) = {}
    var onUserLogout: (suspend (User) -> Unit) = {}
    var onUserDelete: (suspend (User) -> Unit) = {}

    /**
     * Creates a new user.
     * @param user is the user to create.
     * @return the result of the creation with the user.
     */
    suspend fun createUser(user: User): Result<User> =
        if (!repository.isUserLogged()) {
            try {
                Result.success(repository.createUser(user = user))
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.failure(UserAlreadyLoggedException(user))
        }

    /**
     * Logs a user using email and password.
     *
     * @param email is the user email.
     * @param password is the user password.
     * @return the result of the login with the user.
     */
    suspend fun loginWithData(email: String, password: String): Result<User> =
        try {
            val user = repository.loginWithData(email, password)
            onUserLogin(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }

    /**
     * Checks if the user is logged.
     *
     * @return the result of the check on the status of the user.
     */
    suspend fun isUserLogged(): Result<Boolean> =
        try {
            Result.success(repository.isUserLogged())
        } catch (e: Exception) {
            Result.failure(e)
        }

    /**
     * Logs the saved user.
     *
     * @return the result of the login with the user.
     */
    suspend fun automaticLogin(): Result<User> =
        try {
            val user = repository.automaticLogin()
            onUserLogin(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }

    /**
     * Gets the logged user.
     *
     * @return the user.
     */
    suspend fun getLoggedUser(): Result<User> =
        try {
            Result.success(repository.getLoggedUser())
        } catch (e: Exception) {
            Result.failure(e)
        }

    /**
     * Logs out the user.
     *
     * @return the result of the logout with the user.
     */
    suspend fun logoutUser(): Result<User> =
        try {
            val user = repository.logoutUser()
            onUserLogout(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }

    /**
     * Deletes the user.
     *
     * @return the result of the deletion with the user.
     */
    suspend fun deleteUser(): Result<User> =
        try {
            val user = repository.deleteUser()
            onUserDelete(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
}
