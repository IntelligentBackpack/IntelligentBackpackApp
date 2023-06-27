package com.intelligentbackpack.desktopdomain.entities.implementations

import com.intelligentbackpack.desktopdomain.entities.Book

/**
 * Implementation of a book.
 *
 * @property isbn The ISBN of the book.
 */
data class BookImpl(
    override val isbn: String,
) : Book {
    /**
     * The title of the book.
     */
    override lateinit var title: String
        private set

    /**
     * The authors of the book.
     */
    override lateinit var authors: Set<String>
        private set

    /**
     * Constructor for a book.
     *
     * @param isbn The ISBN of the book.
     * @param title The title of the book.
     * @param authors The authors of the book.
     */
    constructor(isbn: String, title: String, authors: Set<String>) : this(isbn) {
        this.title = title
        this.authors = authors
    }
}
