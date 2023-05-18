package com.intelligentbackpack.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.accessdomain.usecase.AccessUseCase
import com.intelligentbackpack.app.App
import com.intelligentbackpack.app.viewdata.UserView
import com.intelligentbackpack.app.viewdata.adapter.UserAdapter.fromDomainToView
import com.intelligentbackpack.app.viewdata.adapter.UserAdapter.fromViewToDomain
import com.intelligentbackpack.desktopdomain.usecase.DesktopUseCase
import kotlinx.coroutines.launch

/**
 * View model for the login.
 */
class LoginViewModel(
    private val accessUseCase: AccessUseCase,
    private val desktopUseCase: DesktopUseCase,
) : ViewModel() {

    /**
     * Live data with the user.
     */
    val user: LiveData<UserView?>
        get() = userImpl

    private val userImpl = MutableLiveData<UserView?>()

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
                    userImpl.postValue(user.fromDomainToView())
                    desktopUseCase.getDesktop()
                        .onSuccess {
                            success(user)
                        }.onFailure {
                            error(it.message ?: "Unknown error")
                        }
                }.onFailure {
                    error(it.message ?: "Unknown error")
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
                    userImpl.postValue(user.fromDomainToView())
                    desktopUseCase.downloadDesktop().onSuccess {
                        success(user)
                    }.onFailure {
                        error(it.message ?: "Unknown error")
                    }
                }
                .onFailure {
                    error(it.message ?: "Unknown error")
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
                    userImpl.postValue(it.fromDomainToView())
                    success(it)
                }
                .onFailure {
                    error(it.message ?: "Unknown error")
                }
        }
    }

    /**
     * Logout the user.
     *
     * @param success the success callback.
     */
    fun logout(success: () -> Unit, error: (String) -> Unit) {
        viewModelScope.launch {
            desktopUseCase.logoutDesktop()
                .onSuccess {
                    accessUseCase.logoutUser()
                        .onSuccess {
                            userImpl.postValue(null)
                            success()
                        }
                        .onFailure {
                            error(it.message ?: "Unknown error")
                        }
                }
                .onFailure {
                    error(it.message ?: "Unknown error")
                }
        }
    }

    /**
     * Delete the user.
     *
     * @param success the success callback.
     */
    fun deleteUser(success: () -> Unit, error: (String) -> Unit) {
        viewModelScope.launch {
            accessUseCase.deleteUser()
                .onSuccess {
                    userImpl.postValue(null)
                    success()
                }.onFailure {
                    error(it.message ?: "Unknown error")
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
                    application.desktopUseCase,
                )
            }
        }
    }
}
