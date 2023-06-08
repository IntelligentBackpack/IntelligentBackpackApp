package com.intelligentbackpack.reminderdomain.usecase

import com.intelligentbackpack.accessdomain.entities.Role
import com.intelligentbackpack.accessdomain.exceptions.ActionNotAllowedForUserException
import com.intelligentbackpack.accessdomain.usecase.AccessUseCase
import com.intelligentbackpack.desktopdomain.entities.BookCopy
import com.intelligentbackpack.desktopdomain.entities.SchoolSupply
import com.intelligentbackpack.desktopdomain.usecase.DesktopUseCase
import com.intelligentbackpack.reminderdomain.adapter.EventAdapter.fromSchoolToReminder
import com.intelligentbackpack.reminderdomain.entitites.ReminderForLesson
import com.intelligentbackpack.reminderdomain.repository.ReminderRepository
import com.intelligentbackpack.schooldomain.entities.calendar.CalendarEvent
import com.intelligentbackpack.schooldomain.usecase.SchoolUseCase
import kotlinx.coroutines.flow.map
import java.time.LocalDate

/**
 * Reminder use case
 *
 * @param accessUseCase the use case for access
 * @param desktopUseCase the use case for desktop
 * @param schoolUseCase the use case for school
 * @param reminderRepository the repository of reminder
 */
class ReminderUseCase(
    private val accessUseCase: AccessUseCase,
    private val desktopUseCase: DesktopUseCase,
    private val schoolUseCase: SchoolUseCase,
    private val reminderRepository: ReminderRepository,
) {

    /**
     * Download the reminder from server
     *
     * @return the result of the operation, if the user isn't a professor or a student exception ActionNotAllowedForUserException
     */
    suspend fun downloadReminder(): Result<Unit> =
        accessUseCase.getLoggedUser().mapCatching { user ->
            if (user.role == Role.PROFESSOR || user.role == Role.STUDENT) {
                reminderRepository.downloadReminder(user)
            } else {
                throw ActionNotAllowedForUserException()
            }
        }

    /**
     * Add a school supply for an event
     *
     * @param reminderForLesson the reminder of the supply for an event
     * @return the result of the operation, if the user isn't a professor exception ActionNotAllowedForUserException
     *
     */
    suspend fun addSchoolSupplyForEvent(
        reminderForLesson: ReminderForLesson,
    ) =
        accessUseCase.getLoggedUser().mapCatching { user ->
            if (user.role == Role.PROFESSOR) {
                val reminder = reminderRepository.getReminder()
                reminder.addBookForLesson(reminderForLesson)
                reminderRepository.addBookForLesson(reminderForLesson, user)
            } else {
                throw ActionNotAllowedForUserException()
            }
        }

    /**
     * Remove a school supply from an event
     *
     * @param reminderForLesson the reminder of the supply for an event to remove
     * @return the result of the operation, if the user isn't a professor exception ActionNotAllowedForUserException
     */
    suspend fun removeSchoolSupplyForEvent(reminderForLesson: ReminderForLesson) =
        accessUseCase.getLoggedUser().mapCatching { user ->
            if (user.role == Role.PROFESSOR) {
                val reminder = reminderRepository.getReminder()
                reminder.removeBookForLesson(reminderForLesson)
                reminderRepository.removeBookForLesson(reminderForLesson, user)
            } else {
                throw ActionNotAllowedForUserException()
            }
        }

    /**
     * Change the period of school supply for an event
     *
     * @param oldReminderForLesson the old reminder of the supply for an event
     * @param newReminderForLesson the new reminder of the supply for an event
     * @return the result of the operation, if the user isn't a professor exception ActionNotAllowedForUserException
     */
    suspend fun changeSchoolSupplyForEvent(
        oldReminderForLesson: ReminderForLesson,
        newReminderForLesson: ReminderForLesson,
    ) =
        accessUseCase.getLoggedUser().mapCatching { user ->
            if (user.role == Role.PROFESSOR) {
                val reminder = reminderRepository.getReminder()
                reminder.changePeriodOfBookForLesson(oldReminderForLesson, newReminderForLesson)
                reminderRepository.changeBookForLesson(oldReminderForLesson, newReminderForLesson, user)
            } else {
                throw ActionNotAllowedForUserException()
            }
        }

    /**
     * Get the school supplies for an event
     *
     * @param calendarEvent the event
     * @return the result of the operation, if the user isn't a professor or a student exception ActionNotAllowedForUserException
     */
    suspend fun getSchoolSuppliesForEvent(calendarEvent: CalendarEvent) =
        accessUseCase.getLoggedUser().mapCatching { user ->
            if (user.role == Role.PROFESSOR || user.role == Role.STUDENT) {
                val reminder = reminderRepository.getReminder()
                calendarEvent.fromSchoolToReminder()?.let { reminder.getBooksForLesson(it) }
                    ?.map { desktopUseCase.getBookCopy(it) }
                    ?.filter { it.isSuccess }
                    ?.mapNotNull { it.getOrNull() }
                    ?.toSet()
                    ?: emptySet()
            } else {
                throw ActionNotAllowedForUserException()
            }
        }

    /**
     * Get the events for a school supply
     *
     * @param supply the school supply
     * @return the result of the operation with the events,
     * if the user isn't a professor or a student exception ActionNotAllowedForUserException
     */
    suspend fun getEventsForSchoolSupply(supply: SchoolSupply) =
        accessUseCase.getLoggedUser().mapCatching {
            if (it.role == Role.PROFESSOR || it.role == Role.STUDENT) {
                val reminder = reminderRepository.getReminder()
                when (supply) {
                    is BookCopy -> reminder.getLessonsForBook(supply.book.isbn)
                    else -> emptySet()
                }
            } else {
                throw ActionNotAllowedForUserException()
            }
        }.mapCatching { reminderEvents ->
            val result = schoolUseCase.userAllSchoolEvents()
            if (result.isSuccess) {
                result.getOrNull()
                    ?.filter { event ->
                        event.fromSchoolToReminder()?.let { it ->
                            reminderEvents.map { it }.contains(it)
                        } ?: false
                    }
                    ?.toSet()
                    ?: emptySet()
            } else {
                throw result.exceptionOrNull()!!
            }
        }

    /**
     * Get the missing school supplies for a date
     *
     * @param date the date to check
     * @return the result of the operation with the missing school supplies,
     * if the user isn't a professor or a student exception ActionNotAllowedForUserException.
     * If there are other errors return the exception
     */
    suspend fun remindUserOfMissingSchoolSupplyForDate(date: LocalDate): Result<Set<SchoolSupply>> =
        accessUseCase.getLoggedUser().mapCatching { user ->
            if (user.role == Role.PROFESSOR || user.role == Role.STUDENT) {
                val desktopResult = desktopUseCase.getDesktop()
                if (desktopResult.isSuccess) {
                    getMissingSchoolSupply(
                        desktopResult.getOrNull()
                            ?.schoolSuppliesInBackpack
                            ?: emptySet(),
                        date,
                    )
                } else {
                    throw desktopResult.exceptionOrNull()!!
                }
            } else {
                throw ActionNotAllowedForUserException()
            }
        }

    /**
     * Subscribe to the missing school supplies for a date
     *
     * @param date the date to check
     * @return the result of the operation with a flow of the missing school supplies,
     * if the user isn't a professor or a student exception ActionNotAllowedForUserException
     */
    suspend fun subscribeToRemindUserOfMissingSchoolSupplyForDate(date: LocalDate) =
        accessUseCase.getLoggedUser().mapCatching { user ->
            if (user.role == Role.PROFESSOR || user.role == Role.STUDENT) {
                desktopUseCase.subscribeToBackpack()
                    .getOrNull()
                    ?.map { inBackpack ->
                        getMissingSchoolSupply(inBackpack, date)
                    }
            } else {
                throw ActionNotAllowedForUserException()
            }
        }

    private suspend fun getMissingSchoolSupply(inBackpack: Set<SchoolSupply>, date: LocalDate): Set<SchoolSupply> {
        val result = schoolUseCase.getUserCalendarEventsForDate(date)
        if (result.isSuccess) {
            val reminder = reminderRepository.getReminder()
            return (
                (
                    result.getOrNull()
                        ?.mapNotNull { it.fromSchoolToReminder() }
                        ?: emptyList()
                    )
                    .flatMap { reminder.getBooksForLessonInDate(it, date) }
                    .map { desktopUseCase.getBookCopy(it) }
                    .filter { it.isSuccess }
                    .mapNotNull { it.getOrNull() }
                    .toSet()
                ) - inBackpack
        } else if (result.isFailure) {
            throw result.exceptionOrNull()!!
        } else {
            throw IllegalStateException()
        }
    }
}
