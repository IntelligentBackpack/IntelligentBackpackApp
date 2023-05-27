package com.intelligentbackpack.schooldomain.entities.calendar.alteration

import com.intelligentbackpack.schooldomain.entities.calendar.CalendarEvent
import java.time.LocalDate

/**
 * Represents an event that reschedules an event from the original calendar.
 *
 * @property originalEvent the event that has been rescheduled
 * @property event the new event
 */
interface RescheduleEvent : AlterationEvent {
    val originalEvent: CalendarEvent

    /**
     * Checks if the event rescheduled was on the given date.
     *
     * @param date the date to check
     * @return true if the event rescheduled was on the given date, false otherwise
     */
    fun wasEventRescheduledOnDate(date: LocalDate): Boolean

    /**
     * Checks if the new event is on the given date.
     *
     * @param date the date to check
     * @return true if the new event is on the given date, false otherwise
     */
    fun isNewEventOnDate(date: LocalDate): Boolean
}
