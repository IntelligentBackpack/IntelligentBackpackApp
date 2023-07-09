package com.intelligentbackpack.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.accessdomain.usecase.AccessUseCase
import com.intelligentbackpack.app.App
import com.intelligentbackpack.app.exceptionhandler.ExceptionMessage.messageOrDefault
import com.intelligentbackpack.app.viewdata.UserView
import com.intelligentbackpack.app.viewdata.adapter.UserAdapter.fromViewToDomain
import kotlinx.coroutines.launch

/**
 * View model for the login.
 */
class LoginViewModel(
    private val accessUseCase: AccessUseCase,
) : ViewModel() {

    /**
     * Login with the given data.
     *
     * @param email the email of the user.
     * @param password the password of the user.
     * @param success the success callback.
     * @param error the error callback.
     */
    fun login(
        email: String,
        password: String,
        success: (user: User) -> Unit,
        error: (error: String) -> Unit,
    ) {
        viewModelScope.launch {
            accessUseCase.loginWithData(email, password)
                .onSuccess { user ->
                    success(user)
                }.onFailure {
                    error(it.messageOrDefault())
                }
        }
    }

    /**
     * Check if the user is logged.
     *
     * @param success the success callback.
     */
    fun isUserLogged(success: (Boolean) -> Unit) {
        viewModelScope.launch {
            accessUseCase.isUserLogged()
                .onSuccess {
                    success(it)
                }.onFailure {
                    success(false)
                }
        }
    }

    /**
     * Try to login automatically.
     *
     * @param success the success callback.
     * @param error the error callback.
     */
    fun tryAutomaticLogin(
        success: (user: User) -> Unit,
        error: (String) -> Unit,
    ) {
        viewModelScope.launch {
            accessUseCase.automaticLogin()
                .onSuccess { user ->
                    success(user)
                }
                .onFailure {
                    error(it.messageOrDefault())
                }
        }
    }

    /**
     * Create a new user.
     *
     * @param data the data of the user.
     * @param success the success callback.
     * @param error the error callback.
     */
    fun createUser(
        data: UserView,
        success: (user: User) -> Unit,
        error: (error: String) -> Unit,
    ) {
        viewModelScope.launch {
            accessUseCase.createUser(data.fromViewToDomain())
                .onSuccess {
                    success(it)
                }
                .onFailure {
                    error(it.messageOrDefault())
                }
        }
    }

    companion object {

        /**
         * Factory for the view model.
         */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = checkNotNull(this[APPLICATION_KEY])
                LoginViewModel(
                    (application as App).accessUseCase,
                )
            }
        }
    }
}
