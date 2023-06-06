package com.intelligentbackpack.reminderdomain.entitites

import com.intelligentbackpack.reminderdomain.adapter.EventAdapter
import com.intelligentbackpack.reminderdomain.entitites.implementation.ReminderForLessonIntervalPeriodImpl
import java.time.LocalDate

/**
 * Reminder for lesson on a interval of time
 *
 * @property startDate initial date for the book to be remembered
 * @property endDate final date for the book to be remembered
 */
interface ReminderForLessonIntervalPeriod : ReminderForLesson {
    val startDate: LocalDate
    val endDate: LocalDate

    companion object {
        /**
         * Create reminder for lesson on specific date
         *
         * @param isbn book isbn
         * @param lesson lesson
         * @param startDate startDate of the reminder
         * @param endDate endDate of the reminder
         * @return reminder for lesson on specific date
         * @throws IllegalArgumentException if the interval is not in the lesson interval
         * @throws IllegalArgumentException if date is not in lesson interval
         * @throws IllegalArgumentException if lesson type is not supported
         */
        fun create(
            isbn: String,
            lesson: EventAdapter.Lesson,
            startDate: LocalDate,
            endDate: LocalDate,
        ): ReminderForLessonIntervalPeriod =
            when (lesson) {
                is EventAdapter.DateLesson -> {
                    throw IllegalArgumentException("Date lesson is not supported for interval period")
                }

                is EventAdapter.WeekEvent -> {
                    if (startDate in lesson.fromData..lesson.toDate && endDate in lesson.fromData..lesson.toDate) {
                        ReminderForLessonIntervalPeriodImpl(isbn, lesson, startDate, endDate)
                    } else {
                        throw IllegalArgumentException("Date is not in lesson interval")
                    }
                }

                else -> {
                    throw IllegalArgumentException("Lesson type is not supported")
                }
            }
    }
}
