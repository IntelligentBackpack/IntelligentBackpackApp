package com.intelligentbackpack.reminderdomain.adapter

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import com.intelligentbackpack.schooldomain.entities.calendar.CalendarEvent as SchoolCalendarEvent
import com.intelligentbackpack.schooldomain.entities.calendar.DateLesson as SchoolDateLesson
import com.intelligentbackpack.schooldomain.entities.calendar.WeekLesson as SchoolWeekLesson

/**
 * EventAdapter is a object that adapts events from school domain to reminder domain.
 */
object EventAdapter {

    /**
     * CalendarEvent is a interface that represents event in calendar.
     *
     * @property startTime is a start time of event.
     * @property endTime is a end time of event.
     */
    interface CalendarEvent {
        val startTime: LocalTime
        val endTime: LocalTime
    }

    /**
     * Lesson is a interface that represents lesson as calendar event.
     *
     * @property subject is a name of subject.
     */
    interface Lesson : CalendarEvent {
        val subject: String
    }

    /**
     * DateEvent is a interface that represents event in calendar that is on specific date.
     *
     * @property date is a date of event.
     */
    interface DateEvent : CalendarEvent {
        val date: LocalDate
    }

    /**
     * WeekEvent is a interface that represents event in calendar that is done in a specific day of week.
     *
     * @property fromDate is a date from which event is done.
     * @property toDate is a date to which event is done.
     * @property dayOfWeek is a day of week when event is done.
     */
    interface WeekEvent : CalendarEvent {
        val fromDate: LocalDate
        val toDate: LocalDate
        val dayOfWeek: DayOfWeek
    }

    /**
     * WeekLesson is a interface that represents lesson as calendar event that is done in a specific day of week.
     */
    interface WeekLesson : WeekEvent, Lesson

    /**
     * DateLesson is a interface that represents lesson as calendar event that is on specific date.
     */
    interface DateLesson : DateEvent, Lesson

    /**
     * DateLessonImpl is a implementation of DateLesson interface.
     *
     * @param startTime is a start time of event.
     * @param endTime is a end time of event.
     * @param subject is a name of subject.
     * @param date is a date of event.
     */
    data class DateLessonImpl(
        override val startTime: LocalTime,
        override val endTime: LocalTime,
        override val subject: String,
        override val date: LocalDate,
    ) : DateLesson

    /**
     * WeekLessonImpl is a implementation of WeekLesson interface.
     *
     * @param startTime is a start time of event.
     * @param endTime is a end time of event.
     * @param subject is a name of subject.
     * @param fromDate is a date from which event is done.
     * @param toDate is a date to which event is done.
     * @param dayOfWeek is a day of week when event is done.
     */
    data class WeekLessonImpl(
        override val startTime: LocalTime,
        override val endTime: LocalTime,
        override val subject: String,
        override val fromDate: LocalDate,
        override val toDate: LocalDate,
        override val dayOfWeek: DayOfWeek,
    ) : WeekLesson

    /**
     * fromSchoolToReminder is a function that converts CalendarEvent from school domain to CalendarEvent from reminder domain.
     * if CalendarEvent is not DateLesson or WeekLesson then null is returned.
     *
     * @return CalendarEvent or null if SchoolCalendarEvent is not supported.
     */
    fun SchoolCalendarEvent.fromSchoolToReminder(): CalendarEvent? =
        when (this) {
            is SchoolDateLesson -> DateLessonImpl(
                this.startTime,
                this.endTime,
                this.subject,
                this.date,
            )

            is SchoolWeekLesson -> WeekLessonImpl(
                this.startTime,
                this.endTime,
                this.subject,
                this.fromDate,
                this.toDate,
                this.day,
            )

            else -> null
        }
}
