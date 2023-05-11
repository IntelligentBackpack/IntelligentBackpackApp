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

class LoginViewModel(
    private val accessUseCase: AccessUseCase
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
        viewModelScope.launch(Dispatchers.IO) {
            accessUseCase.loginWithData(email, password, {
                viewModelScope.launch(Dispatchers.Main) {
                    userImpl.postValue(it)
                    success(it)
                }
            }, {
                viewModelScope.launch(Dispatchers.Main) {
                    error(it.message ?: "Unknown error")
                }
            })
        }
    }

    fun tryAutomaticLogin(
        success: (user: User) -> Unit,
        error: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            accessUseCase.isUserLogged({ logged ->
                if (logged) {
                    viewModelScope.launch(Dispatchers.IO) {
                        accessUseCase.automaticLogin({
                            viewModelScope.launch(Dispatchers.Main) {
                                userImpl.postValue(it)
                                success(it)
                            }
                        }, {
                            viewModelScope.launch(Dispatchers.Main) {
                                error()
                            }
                        })

                    }
                }
            }, {
            })
        }
    }

    fun createUser(
        data: UserView, success:
            (user: User) -> Unit,
        error: (error: String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            accessUseCase.createUser(
                User.build {
                    email = data.email
                    password = data.password
                    name = data.name
                    surname = data.surname
                }, {
                    viewModelScope.launch(Dispatchers.Main) {
                        userImpl.postValue(it)
                        success(it)
                    }
                }, {
                    viewModelScope.launch(Dispatchers.Main) {
                        error(it.message ?: "Unknown error")
                    }
                }
            )
        }
    }


    fun logout(success: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            accessUseCase.logoutUser({
                viewModelScope.launch(Dispatchers.Main) {
                    userImpl.postValue(null)
                    success()
                }
            }, {
            })
        }
    }

    fun deleteUser(success: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            accessUseCase.deleteUser({
                viewModelScope.launch(Dispatchers.Main) {
                    userImpl.postValue(null)
                    success()
                }
            }, {
            })
        }

    }

    companion object {

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // Get the Application object from extras
                val application = checkNotNull(this[APPLICATION_KEY])
                LoginViewModel(
                    (application as App).accessUseCase
                )
            }
        }
    }
}
