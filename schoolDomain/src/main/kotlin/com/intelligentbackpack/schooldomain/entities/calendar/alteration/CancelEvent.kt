package com.intelligentbackpack.schooldomain.entities.calendar.alteration

import java.time.LocalDate

/**
 * Represents an event that cancel an event from the original calendar.
 *
 * @property event the event that has been cancelled
 */
interface CancelEvent : AlterationEvent {
    /**
     * Checks if the event is cancelled on the given date.
     *
     * @param date the date to check
     * @return true if the event is cancelled on the given date, false otherwise
     */
    fun isEventCancelledOnDate(date: LocalDate): Boolean
}
