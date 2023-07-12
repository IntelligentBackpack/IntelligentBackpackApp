package com.intelligentbackpack.app.viewdata

import android.os.Parcelable
import com.intelligentbackpack.desktopdomain.entities.Author
import kotlinx.parcelize.Parcelize

/**
 * View data class for a book.
 *
 * @property isbn the isbn of the book.
 * @property title the title of the book.
 * @property authors the authors of the book.
 */
@Parcelize
data class BookView(
    val isbn: String,
    val title: String,
    val authors: Set<Author>,
) : Parcelable
