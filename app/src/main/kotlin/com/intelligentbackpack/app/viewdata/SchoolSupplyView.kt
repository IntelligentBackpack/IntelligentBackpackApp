package com.intelligentbackpack.app.viewdata

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * View data class for a school supply.
 */
@Parcelize
data class SchoolSupplyView(
    val rfidCode: String,
    val type: String,
    val book: BookView? = null
) : Parcelable
