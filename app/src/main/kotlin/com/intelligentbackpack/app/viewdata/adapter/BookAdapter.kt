package com.intelligentbackpack.app.viewdata.adapter

import com.intelligentbackpack.app.viewdata.BookView
import com.intelligentbackpack.desktopdomain.entities.Book

/**
 * Adapter to convert a [Book] to a [BookView].
 */
object BookAdapter {

    /**
     * Converts a [Book] to a [BookView].
     */
    fun Book.fromDomainToView() = BookView(
        isbn = isbn,
        title = title,
        authors = authors,
    )

    /**
     * Converts a [BookView] to a [Book].
     */
    fun BookView.fromViewToDomain() = Book.build {
        isbn = this@fromViewToDomain.isbn
        title = this@fromViewToDomain.title
        authors = this@fromViewToDomain.authors
    }
}
