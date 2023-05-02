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
import com.intelligentbackpack.app.viewdata.UserView
import kotlinx.coroutines.launch

class LoginViewModel(
    private val accessUseCase: AccessUseCase,
) : ViewModel() {

    var user: User? = null
    fun login(
        email: String,
        password: String,
        success: (user: User) -> Unit,
        error: (error: String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                user = accessUseCase.loginWithData(email, password)
                success(user!!)
            } catch (e: Exception) {
                error(e.message ?: "Unknown error")
            }
        }
    }

    fun tryAutomaticLogin(
        success: (user: User) -> Unit,
    ) {
        viewModelScope.launch {
            if (accessUseCase.isUserLogged()) {
                user = accessUseCase.automaticLogin()
                success(user!!)
            }
        }
    }

    fun createUser(
        data: UserView, success:
            (user: User) -> Unit,
        error: (error: String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val user = accessUseCase.createUser(
                    User.build {
                        email = data.email
                        password = data.password
                        name = data.name
                        surname = data.surname
                    }
                )
                success(user)
            } catch (e: Exception) {
                error(e.message ?: "Unknown error")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            accessUseCase.logoutUser()
            user = null
        }
    }

    companion object {

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // Get the Application object from extras
                val application = checkNotNull(this[APPLICATION_KEY])
                LoginViewModel(
                    (application as App).accessUseCase,
                )
            }
        }
    }
}
