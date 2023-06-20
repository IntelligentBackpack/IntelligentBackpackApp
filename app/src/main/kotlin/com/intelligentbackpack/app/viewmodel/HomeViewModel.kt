package com.intelligentbackpack.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.intelligentbackpack.accessdomain.usecase.AccessUseCase
import com.intelligentbackpack.app.App
import com.intelligentbackpack.app.viewdata.UserView
import com.intelligentbackpack.app.viewdata.adapter.UserAdapter.fromDomainToView
import com.intelligentbackpack.reminderdomain.usecase.ReminderUseCase
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(
    private val accessUseCase: AccessUseCase,
    private val reminderUseCase: ReminderUseCase,
) : ViewModel() {

    /**
     * Live data with the user.
     */
    val user: LiveData<UserView?>
        get() = userImpl

    private val userImpl = MutableLiveData<UserView?>()

    val missing: LiveData<Int>
        get() = missingImpl

    private val missingImpl = MutableLiveData(0)

    /**
     * Gets the user.
     *
     * @param success the success callback.
     */
    fun getUser(success: (UserView) -> Unit, error: (String) -> Unit) {
        viewModelScope.launch {
            accessUseCase.getLoggedUser()
                .onSuccess { user ->
                    userImpl.postValue(user.fromDomainToView())
                    success(user.fromDomainToView())
                }
                .onFailure {
                    error(it.message ?: "Unknown error")
                }
        }
    }

    fun getReminders(error: (String) -> Unit) {
        viewModelScope.launch {
            reminderUseCase.subscribeToRemindUserOfMissingSchoolSupplyForDate(LocalDate.now().plusDays(1))
                .onSuccess { flow ->
                    flow?.collect { reminders ->
                        missingImpl.postValue(reminders.size)
                    }
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
            accessUseCase.logoutUser()
                .onSuccess {
                    userImpl.postValue(null)
                    success()
                }
                .onFailure {
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
                val application = checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                HomeViewModel(
                    (application as App).accessUseCase,
                    application.reminderUseCase,
                )
            }
        }
    }
}
