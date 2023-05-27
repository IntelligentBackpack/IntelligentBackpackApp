package com.intelligentbackpack.schooldomain.entities.calendar.alteration

import java.time.LocalDate

/**
 * Represents an alteration event that alters the original calendar for a single day
 *
 * @property date the date in which the alteration occurs
 */
interface SingleDayAlteration {
    val date: LocalDate
}
