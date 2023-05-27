package com.intelligentbackpack.schooldomain.policy

import com.intelligentbackpack.schooldomain.entities.calendar.CalendarEvent
import com.intelligentbackpack.schooldomain.entities.calendar.DateEvent
import com.intelligentbackpack.schooldomain.entities.calendar.TimeTable
import com.intelligentbackpack.schooldomain.entities.calendar.WeekEvent
import com.intelligentbackpack.schooldomain.entities.calendar.WeekLesson

/**
 * A policy that checks if a calendar event is valid.
 */
object CalendarPolicy {

    /**
     * check if two events are overlapping in time
     *
     * @param calendarEvent the first event
     * @param otherCalendarEvent the second event
     * @return true if the events are overlapping in time otherwise false,
     * the events are overlapping if the start time of one event is in the range of the other event (end time excluded)
     */
    @OptIn(ExperimentalStdlibApi::class)
    fun isOverlappingTime(
        calendarEvent: CalendarEvent,
        otherCalendarEvent: CalendarEvent,
    ): Boolean {
        return calendarEvent.startTime in otherCalendarEvent.startTime.rangeUntil(otherCalendarEvent.endTime) ||
            otherCalendarEvent.startTime in calendarEvent.startTime.rangeUntil(calendarEvent.endTime)
    }

    /**
     * check if two events are overlapping in date
     *
     * @param calendarEvent the first event (must be a week event)
     * @param otherCalendarEvent the second event (must be a date event)
     * @return true if the events are overlapping in date otherwise false
     * (the events are overlapping if the date of the date event is in the range of the week event)
     */
    fun isOverlappingDate(
        calendarEvent: WeekEvent,
        otherCalendarEvent: DateEvent,
    ): Boolean {
        return otherCalendarEvent.date in calendarEvent.fromDate.rangeTo(calendarEvent.toDate)
    }

    /**
     * check if two events are overlapping in date
     *
     * @param calendarEvent the first event (must be a date event)
     * @param otherCalendarEvent the second event (must be a week event)
     * @return true if the events are overlapping in date otherwise false
     * (the events are overlapping if the date of the date event is in the range of the week event)
     */
    fun isOverlappingDate(
        calendarEvent: DateEvent,
        otherCalendarEvent: WeekEvent,
    ): Boolean {
        return isOverlappingDate(otherCalendarEvent, calendarEvent)
    }

    /**
     * check if two events are overlapping in date
     *
     * @param calendarEvent the first event (must be a date event)
     * @param otherCalendarEvent the second event (must be a date event)
     * @return true if the events are overlapping in date otherwise false
     * (the events are overlapping if the date is the same)
     */
    fun isOverlappingDate(
        calendarEvent: DateEvent,
        otherCalendarEvent: DateEvent,
    ): Boolean {
        return calendarEvent.date == otherCalendarEvent.date
    }

    /**
     * check if two events are overlapping in date
     *
     * @param calendarEvent the first event (must be a week event)
     * @param otherCalendarEvent the second event (must be a week event)
     * @return true if the events are overlapping in date otherwise false
     * (the events are overlapping if the date of one event is in the range of the other event)
     */
    fun isOverlappingDate(
        calendarEvent: WeekEvent,
        otherCalendarEvent: WeekEvent,
    ): Boolean {
        return calendarEvent.fromDate in otherCalendarEvent.fromDate.rangeTo(otherCalendarEvent.toDate) ||
            calendarEvent.toDate in otherCalendarEvent.fromDate.rangeTo(otherCalendarEvent.toDate) ||
            otherCalendarEvent.fromDate in calendarEvent.fromDate.rangeTo(calendarEvent.toDate) ||
            otherCalendarEvent.toDate in calendarEvent.fromDate.rangeTo(calendarEvent.toDate)
    }

    /**
     * check if a set of new lessons is overlapping with the lessons in the time table (only the lessons that are not filtered)
     *
     * @param newLessons the new lessons
     * @param timeTable the time table
     * @param filter the filter for the lessons in the time table
     * @return true if the new lessons are overlapping with the lessons in the time table otherwise false
     * (the lessons are overlapping if the date and time are the same, or in the same interval)
     * @see isOverlappingDate
     * @see isOverlappingTime
     */
    fun areLessonsInTimeTableOverlapping(
        newLessons: Set<WeekLesson>,
        timeTable: TimeTable,
        filter: (WeekLesson) -> Boolean = { true },
    ): Boolean =
        newLessons.any { newLesson ->
            (
                (timeTable[newLesson.day] ?: listOf()) +
                    newLessons.filter {
                        it.day == newLesson.day
                    }.filter(filter)
                        .toSet() - newLesson
                )
                .filter { isOverlappingDate(it, newLesson) }
                .any {
                    isOverlappingTime(it, newLesson)
                }
        }
}
