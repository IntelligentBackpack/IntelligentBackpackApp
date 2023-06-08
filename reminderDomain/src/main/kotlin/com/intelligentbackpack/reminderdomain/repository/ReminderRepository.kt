package com.intelligentbackpack.reminderdomain.repository

import com.intelligentbackpack.reminderdomain.entitites.Reminder
import com.intelligentbackpack.reminderdomain.entitites.ReminderForLesson

/**
 * Reminder repository.
 */
interface ReminderRepository {

    /**
     * Download reminder from server.
     */
    suspend fun downloadReminder()

    /**
     * Get reminder.
     *
     * @return reminder.
     */
    suspend fun getReminder(): Reminder

    /**
     * Add book for lesson.
     *
     * @param reminderForLesson reminder for lesson.
     */
    suspend fun addBookForLesson(reminderForLesson: ReminderForLesson)

    /**
     * Remove book for lesson.
     *
     * @param reminderForLesson reminder for lesson.
     */
    suspend fun removeBookForLesson(reminderForLesson: ReminderForLesson)

    /**
     * Change the period of book used in the lesson for lesson.
     *
     * @param reminderForLesson reminder for lesson.
     * @param newReminderForLesson new reminder for lesson.
     */
    suspend fun changeBookForLesson(reminderForLesson: ReminderForLesson, newReminderForLesson: ReminderForLesson)
}
