package com.intelligentbackpack.app.viewdata

import android.os.Parcelable
import com.intelligentbackpack.desktopdomain.entities.Author
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookView(
    val isbn: String,
    val title: String,
    val authors: Set<Author>
): Parcelable
