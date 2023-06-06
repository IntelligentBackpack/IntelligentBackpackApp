package com.intelligentbackpack.reminderdomain.entitites.implementation

import com.intelligentbackpack.reminderdomain.adapter.EventAdapter
import com.intelligentbackpack.reminderdomain.entitites.ReminderForLessonDate
import java.time.LocalDate

/**
 * @param isbn - ISBN of the book
 * @param lesson - lesson for which the book is assigned
 * @param date - date of the reminder
 */
internal data class ReminderForLessonDateImpl(
    override val isbn: String,
    override val lesson: EventAdapter.Lesson,
    override val date: LocalDate,
) : ReminderForLessonDate {

    /**
     * @param date - date to check
     * @return true if date is equal to lesson date
     */
    override fun isInInterval(date: LocalDate): Boolean =
        this.date == date
}
