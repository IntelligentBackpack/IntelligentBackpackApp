package com.intelligentbackpack.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.intelligentbackpack.app.App
import com.intelligentbackpack.app.viewdata.BookView
import com.intelligentbackpack.app.viewdata.EventView
import com.intelligentbackpack.app.viewdata.SchoolSupplyView
import com.intelligentbackpack.app.viewdata.adapter.BookAdapter.fromDomainToView
import com.intelligentbackpack.app.viewdata.adapter.EventAdapter.fromDomainToView
import com.intelligentbackpack.app.viewdata.adapter.SchoolSupplyAdapter.fromDomainToView
import com.intelligentbackpack.desktopdomain.entities.BookCopy
import com.intelligentbackpack.desktopdomain.usecase.DesktopUseCase
import com.intelligentbackpack.reminderdomain.adapter.EventAdapter
import com.intelligentbackpack.reminderdomain.adapter.EventAdapter.fromSchoolToReminder
import com.intelligentbackpack.reminderdomain.entitites.implementation.ReminderForLessonDateImpl
import com.intelligentbackpack.reminderdomain.entitites.implementation.ReminderForLessonIntervalPeriodImpl
import com.intelligentbackpack.reminderdomain.usecase.ReminderUseCase
import com.intelligentbackpack.schooldomain.entities.calendar.CalendarEvent
import com.intelligentbackpack.schooldomain.usecase.SchoolUseCase
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * View model for the calendar screen.
 *
 * @param schoolUseCase the school use case.
 * @param reminderUseCase the reminder use case.
 * @param desktopUseCase the desktop use case.
 */
class CalendarViewModel(
    private val schoolUseCase: SchoolUseCase,
    private val reminderUseCase: ReminderUseCase,
    private val desktopUseCase: DesktopUseCase,
) : ViewModel() {

    /**
     * Live data with the events.
     */
    val events: LiveData<List<EventView>>
        get() = eventsImpl.map { events ->
            events.mapIndexedNotNull { index, event ->
                event.fromDomainToView(index)
            }
        }

    private val eventsImpl = MutableLiveData<List<CalendarEvent>>()

    /**
     * Gets the events for the given date.
     *
     * @param date the date.
     * @param success the success callback.
     */
    fun getDateCalendar(date: LocalDate, success: (List<EventView>) -> Unit, error: (String) -> Unit) {
        viewModelScope.launch {
            schoolUseCase.getUserCalendarEventsForDate(date)
                .onSuccess { calendar ->
                    eventsImpl.postValue(calendar)
                    success(
                        calendar.mapIndexedNotNull { index, event ->
                            event.fromDomainToView(index)
                        },
                    )
                }
                .onFailure {
                    error(it.message ?: "Unknown error")
                }
        }
    }

    /**
     * Gets the event for the given index.
     *
     * @param index the index.
     * @return the event.
     */
    fun getEventAt(index: Int): EventView? {
        return eventsImpl.value?.getOrNull(index)?.fromDomainToView(index)
    }

    /**
     * Gets the event for the given index.
     *
     * @param index the index.
     * @param success the success callback.
     * @param error the error callback.
     */
    fun getSuppliesForEvent(
        index: Int,
        success: (List<SchoolSupplyView>) -> Unit,
        error: (String) -> Unit,
    ) {
        viewModelScope.launch {
            eventsImpl.value?.getOrNull(index)?.let { event ->
                reminderUseCase.getSchoolSuppliesForEvent(event)
                    .onSuccess { supplies ->
                        success(
                            supplies.map { supply ->
                                supply.fromDomainToView()
                            },
                        )
                    }
                    .onFailure {
                        error(it.message ?: "Unknown error")
                    }
            }
        }
    }

    /**
     * Adds a school supply to the event.
     *
     * @param index the index.
     * @param isbn the isbn.
     * @param fromDate the from date.
     * @param toDate the to date.
     * @param success the success callback.
     * @param error the error callback.
     */
    fun addSchoolSupplyToEvent(
        index: Int,
        isbn: String,
        fromDate: LocalDate,
        toDate: LocalDate,
        success: () -> Unit,
        error: (String) -> Unit,
    ) {
        viewModelScope.launch {
            eventsImpl.value?.getOrNull(index)?.let { event ->
                val reminderEvent = event.fromSchoolToReminder() as EventAdapter.Lesson
                val reminder = if (fromDate == toDate) {
                    ReminderForLessonDateImpl(
                        isbn = isbn,
                        lesson = reminderEvent,
                        date = fromDate,
                    )
                } else {
                    ReminderForLessonIntervalPeriodImpl(
                        isbn = isbn,
                        lesson = reminderEvent,
                        startDate = fromDate,
                        endDate = toDate,
                    )
                }
                reminderUseCase.addSchoolSupplyForEvent(reminder)
                    .onSuccess {
                        success()
                    }
                    .onFailure {
                        error(it.message ?: "Unknown error")
                    }
            }
        }
    }

    /**
     * Gets all the books.
     *
     * @param success the success callback.
     * @param error the error callback.
     */
    fun getAllBooks(
        success: (List<BookView>) -> Unit,
        error: (String) -> Unit,
    ) {
        viewModelScope.launch {
            desktopUseCase.getDesktop()
                .onSuccess { desktop ->
                    val test = desktop.schoolSupplies
                        .filterIsInstance<BookCopy>()
                        .map { it.book }
                        .map { it.fromDomainToView() }
                        .distinct()
                    success(
                        test,
                    )
                }
                .onFailure {
                    error(it.message ?: "Unknown error")
                }
        }
    }

    /**
     * Checks if the user is a professor.
     *
     * @param success the success callback.
     * @param error the error callback.
     */
    fun isUserProfessor(success: (Boolean) -> Unit, error: (String) -> Unit) {
        viewModelScope.launch {
            reminderUseCase.isUserProfessor()
                .onSuccess {
                    success(it)
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
                CalendarViewModel(
                    (application as App).schoolUseCase,
                    application.reminderUseCase,
                    application.desktopUseCase,
                )
            }
        }
    }
}