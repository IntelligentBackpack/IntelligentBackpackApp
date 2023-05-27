package com.intelligentbackpack.schooldomain.entities.calendar

import java.time.DayOfWeek
import java.time.LocalDate

/**
 * An event that happens every week at the same time on the same day of the week for a certain period of time.
 *
 * @property day the day of the week the event happens
 * @property fromDate the date from which the event happens
 * @property toDate the date until which the event happens
 */
interface WeekEvent : CalendarEvent {
    val day: DayOfWeek
    val fromDate: LocalDate
    val toDate: LocalDate
}
