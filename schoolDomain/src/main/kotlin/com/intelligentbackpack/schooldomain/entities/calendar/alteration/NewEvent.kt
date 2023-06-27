package com.intelligentbackpack.schooldomain.entities.calendar.alteration

import java.time.LocalDate

/**
 * Represents a new event that is added to the original calendar.
 *
 * @property event the new event
 */
interface NewEvent : AlterationEvent {

    /**
     * Checks if the new event is on the given date.
     *
     * @param date the date to check
     * @return true if the new event is on the given date, false otherwise
     */
    fun isNewEventOnDate(date: LocalDate): Boolean
}
