package com.intelligentbackpack.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.intelligentbackpack.app.App
import com.intelligentbackpack.app.exceptionhandler.ExceptionMessage.messageOrDefault
import com.intelligentbackpack.app.viewdata.SchoolSupplyView
import com.intelligentbackpack.app.viewdata.adapter.SchoolSupplyAdapter.fromDomainToView
import com.intelligentbackpack.reminderdomain.usecase.ReminderUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * View model for the reminder screen.
 *
 * @param reminderUseCase the reminder use case.
 */
class ReminderViewModel(
    private val reminderUseCase: ReminderUseCase,
) : ViewModel() {

    /**
     * Live data with the missing school supplies.
     */
    val missing: LiveData<List<SchoolSupplyView>>
        get() = missingImpl

    private val missingImpl = MutableLiveData<List<SchoolSupplyView>>()

    private var lastJob: Job? = null

    /**
     * Checks if the backpack is associated.
     *
     * @param success the success callback.
     * @param error the error callback.
     */
    fun isBackpackAssociated(
        success: (Boolean) -> Unit,
        error: (String) -> Unit,
    ) {
        viewModelScope.launch {
            reminderUseCase.isBackpackAssociated()
                .onSuccess { success(it) }
                .onFailure { error(it.messageOrDefault()) }
        }
    }

    /**
     * Gets the reminders for the given date.
     *
     * @param date the date.
     * @param error the error callback.
     */
    fun getReminders(date: LocalDate, error: (String) -> Unit) {
        lastJob?.cancel()
        lastJob = viewModelScope.launch {
            reminderUseCase.subscribeToRemindUserOfMissingSchoolSupplyForDate(date)
                .onSuccess { flow ->
                    flow?.cancellable()?.collect { reminders ->
                        missingImpl.postValue(reminders.map { it.fromDomainToView() })
                    }
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
                val application = checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                ReminderViewModel(
                    (application as App).reminderUseCase,
                )
            }
        }
    }
}
