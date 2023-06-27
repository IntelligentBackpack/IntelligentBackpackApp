package com.intelligentbackpack.reminderdomain.entitites

import com.intelligentbackpack.reminderdomain.adapter.EventAdapter
import com.intelligentbackpack.reminderdomain.entitites.implementation.ReminderForLessonDateImpl
import java.time.LocalDate

/**
 * Reminder for lesson on specific date
 *
 * @property date date of lesson
 */
interface ReminderForLessonDate : ReminderForLesson {
    val date: LocalDate

    companion object {

        /**
         * Create reminder for lesson on specific date
         *
         * @param isbn book isbn
         * @param lesson lesson
         * @param date date the reminder
         * @return reminder for lesson on specific date
         * @throws IllegalArgumentException if date is not equal to lesson date
         * @throws IllegalArgumentException if date is not in lesson interval
         * @throws IllegalArgumentException if lesson type is not supported
         */
        fun create(isbn: String, lesson: EventAdapter.Lesson, date: LocalDate): ReminderForLessonDate =
            when (lesson) {
                is EventAdapter.DateLesson -> {
                    if (lesson.date == date) {
                        ReminderForLessonDateImpl(isbn, lesson, date)
                    } else {
                        throw IllegalArgumentException("Date is not equal to lesson date")
                    }
                }

                is EventAdapter.WeekEvent -> {
                    if (date in lesson.fromDate..lesson.toDate) {
                        ReminderForLessonDateImpl(isbn, lesson, date)
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
