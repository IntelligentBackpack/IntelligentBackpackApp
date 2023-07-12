package com.intelligentbackpack.app.viewdata

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * View data for the reminder.
 *
 * @property schoolSupplyView the school supply of the reminder.
 * @property eventView the event of the reminder.
 * @property fromDate the start date of the reminder.
 * @property toDate the end date of the reminder.
 * @property date the date of the reminder.
 *
 * Either [fromDate] and [toDate] or [date] must be not null.
 *
 */
@Parcelize
data class ReminderView(
    val schoolSupplyView: SchoolSupplyView,
    val eventView: EventView,
    val fromDate: String?,
    val toDate: String?,
    val date: String?,
) : Parcelable
