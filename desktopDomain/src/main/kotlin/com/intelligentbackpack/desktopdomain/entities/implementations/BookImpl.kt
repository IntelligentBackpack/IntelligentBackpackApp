package com.intelligentbackpack.desktopdomain.entities.implementations

import com.intelligentbackpack.desktopdomain.entities.Book

data class BookImpl(
    override val isbn: String
) : Book {
    override lateinit var title: String
        private set

    override lateinit var authors: Set<String>
        private set

    constructor(isbn: String, title: String, authors: Set<String>) : this(isbn) {
        this.title = title
        this.authors = authors
    }
}
