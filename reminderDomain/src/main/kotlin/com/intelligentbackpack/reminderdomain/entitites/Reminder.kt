package com.intelligentbackpack.reminderdomain.entitites

import com.intelligentbackpack.reminderdomain.adapter.EventAdapter
import com.intelligentbackpack.reminderdomain.entitites.implementation.ReminderImpl
import java.time.LocalDate

/**
 * Reminder for lesson.
 */
interface Reminder {

    /**
     * Add book for lesson.
     *
     * @param reminderForLesson reminder for lesson.
     * @return reminder.
     */
    fun addBookForLesson(reminderForLesson: ReminderForLesson): Reminder

    /**
     * Change period of book for lesson.
     *
     * @param oldReminderForLesson old reminder for lesson.
     * @param newReminderForLesson new reminder for lesson.
     * @return reminder.
     */
    fun changePeriodOfBookForLesson(
        oldReminderForLesson: ReminderForLesson,
        newReminderForLesson: ReminderForLesson,
    ): Reminder

    /**
     * Remove book for lesson.
     *
     * @param reminderForLesson reminder for lesson.
     * @return reminder.
     */
    fun removeBookForLesson(reminderForLesson: ReminderForLesson): Reminder

    /**
     * Get books for lesson.
     *
     * @param event event.
     * @return the reminder for lesson.
     */
    fun getBooksForLesson(event: EventAdapter.CalendarEvent): Set<ReminderForLesson>

    /**
     * Get lessons for book.
     *
     * @param isbn isbn.
     * @return the reminder for the book.
     */
    fun getLessonsForBook(isbn: String): Set<ReminderForLesson>

    /**
     * Get books for lesson in date.
     *
     * @param event event.
     * @param date date.
     * @return books for lesson in date.
     */
    fun getBooksForLessonInDate(event: EventAdapter.CalendarEvent, date: LocalDate): Set<String>

    companion object {
        /**
         * Create reminder.
         *
         * @return reminder.
         */
        fun create(): Reminder = ReminderImpl()

        /**
         * Create reminder.
         *
         * @param set set of reminder for lesson.
         * @return reminder.
         */
        fun create(
            set: Set<ReminderForLesson>,
        ): Reminder =
            ReminderImpl(
                booksForLesson = set.groupBy { it.lesson }.mapValues { it.value.toSet() },
                lessonsForBook = set.groupBy { it.isbn }.mapValues { it.value.toSet() },
            )
    }
}
