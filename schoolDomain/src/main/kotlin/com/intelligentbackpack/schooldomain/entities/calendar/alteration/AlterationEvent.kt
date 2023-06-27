package com.intelligentbackpack.schooldomain.entities.calendar.alteration

import com.intelligentbackpack.schooldomain.entities.calendar.CalendarEvent

/**
 * Represents an event that alters the original calendar.
 *
 * @property event the event that alters the original calendar
 */
interface AlterationEvent {
    val event: CalendarEvent
}
