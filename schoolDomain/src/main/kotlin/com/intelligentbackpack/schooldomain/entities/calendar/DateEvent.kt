package com.intelligentbackpack.schooldomain.entities.calendar

import java.time.LocalDate

/**
 * An event that happens on a specific date.
 *
 * @property date the date of the event
 */
interface DateEvent : CalendarEvent {
    val date: LocalDate
}
