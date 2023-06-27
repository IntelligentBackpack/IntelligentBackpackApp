package com.intelligentbackpack.schooldomain.entities.calendar

import java.time.LocalTime

/**
 * An event in the calendar.
 *
 * @property startTime the time the event starts
 * @property endTime the time the event ends
 */
interface CalendarEvent {
    val startTime: LocalTime
    val endTime: LocalTime
}
