package com.intelligentbackpack.app.viewdata

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * View data for the event.
 *
 * @property index the index of the event in the list.
 * @property from the start date of the event.
 * @property to the end date of the event.
 * @property date the date of the event.
 * @property subject the subject of the event.
 * @property module the module of the event.
 * @property day the day of the event.
 * @property professor the email of  the professor of the event.
 * @property professorName the name of the professor of the event.
 * @property professorSurname the surname of the professor of the event.
 * @property startTime the start time of the event.
 * @property endTime the end time of the event.
 * @property studentClass the class of the event.
 *
 * Either [from] and [to] or [date] must be not null.
 *
 */
@Parcelize
data class EventView(
    val index: Int,
    val from: String?,
    val to: String?,
    val date: String?,
    val subject: String,
    val module: String,
    val day: String,
    val professor: String,
    val professorName: String,
    val professorSurname: String,
    val startTime: String,
    val endTime: String,
    val studentClass: String,
) : Parcelable
