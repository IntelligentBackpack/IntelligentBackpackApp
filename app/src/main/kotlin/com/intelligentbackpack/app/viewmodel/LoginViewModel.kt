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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val accessUseCase: AccessUseCase,
) : ViewModel() {

    val user: LiveData<User?>
        get() = userImpl

    private val userImpl = MutableLiveData<User?>()
    fun login(
        email: String,
        password: String,
        success: (user: User) -> Unit,
        error: (error: String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    userImpl.postValue(accessUseCase.loginWithData(email, password))
                }
                success(userImpl.value!!)
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
                withContext(Dispatchers.IO) {
                    userImpl.postValue(accessUseCase.automaticLogin())
                }
                success(userImpl.value!!)
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
                withContext(Dispatchers.IO) {
                    userImpl.postValue(accessUseCase.createUser(
                        User.build {
                            email = data.email
                            password = data.password
                            name = data.name
                            surname = data.surname
                        }
                    ))
                }
                success(userImpl.value!!)
            } catch (e: Exception) {
                error(e.message ?: "Unknown error")
            }
        }
    }

    fun logout(success: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                accessUseCase.logoutUser()
            }
            userImpl.postValue(null)
            success()
        }
    }

    fun deleteUser(success: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                accessUseCase.deleteUser()
            }
            userImpl.postValue(null)
            success()
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
