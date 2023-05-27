package com.intelligentbackpack.schooldomain.entities.calendar.alteration

import java.time.LocalDate

/**
 * Represents an alteration event that alters the original calendar for an interval of days
 *
 * @property initialDate the initial date of the interval
 * @property finalDate the final date of the interval
 */
interface IntervalOfDaysAlteration : AlterationEvent {
    val initialDate: LocalDate
    val finalDate: LocalDate
}
