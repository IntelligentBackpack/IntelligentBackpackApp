package com.intelligentbackpack.reminderdomain.entitites.implementation

import com.intelligentbackpack.reminderdomain.adapter.EventAdapter
import com.intelligentbackpack.reminderdomain.entitites.ReminderForLessonIntervalPeriod
import java.time.LocalDate

/**
 * @param isbn - ISBN of the book
 * @param lesson - lesson for which the book is assigned
 * @param startDate - initial date of the reminder
 * @param endDate - final date of the reminder
 */
data class ReminderForLessonIntervalPeriodImpl(
    override val isbn: String,
    override val lesson: EventAdapter.Lesson,
    override val startDate: LocalDate,
    override val endDate: LocalDate,
) : ReminderForLessonIntervalPeriod {

    /**
     * @param date - date to check
     * @return true if date is in the interval
     */
    override fun isInInterval(date: LocalDate): Boolean =
        date in startDate..endDate
}
