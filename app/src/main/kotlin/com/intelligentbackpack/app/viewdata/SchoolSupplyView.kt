package com.intelligentbackpack.app.viewdata

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * View data class for a school supply.
 *
 * @property rfidCode the rfid code of the school supply.
 * @property type the type of the school supply.
 * @property book the book of the school supply.
 */
@Parcelize
data class SchoolSupplyView(
    val rfidCode: String,
    val type: String,
    val book: BookView? = null,
) : Parcelable
