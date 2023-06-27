package com.intelligentbackpack.reminderdomain.entitites

import com.intelligentbackpack.reminderdomain.adapter.EventAdapter
import java.time.LocalDate

/**
 * Reminder of a book for a lesson
 *
 * @property isbn book's isbn
 * @property lesson lesson in which the book is requested
 */
interface ReminderForLesson {
    val isbn: String
    val lesson: EventAdapter.Lesson

    /**
     * Check if the reminder is valid for the [date]
     *
     * @param date the date to check
     * @return true if the reminder is active for the date, false otherwise
     */
    fun isInInterval(date: LocalDate): Boolean
}
