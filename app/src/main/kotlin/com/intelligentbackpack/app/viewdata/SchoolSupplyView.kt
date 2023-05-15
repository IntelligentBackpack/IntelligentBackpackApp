package com.intelligentbackpack.app.viewdata

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.intelligentbackpack.desktopdomain.entities.SchoolSupply
import com.intelligentbackpack.desktopdomain.entities.SchoolSupplyType

/**
 * View data class for a school supply.
 */
@Parcelize
data class SchoolSupplyView(
    override val rfidCode: String,
    override val type: SchoolSupplyType,
    val book: BookView? = null
) : SchoolSupply, Parcelable
