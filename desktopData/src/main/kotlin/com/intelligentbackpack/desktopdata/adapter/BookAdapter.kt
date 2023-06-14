package com.intelligentbackpack.desktopdata.adapter

import com.intelligentbackpack.desktopdata.adapter.AuthorAdapter.fromDBToDomain
import com.intelligentbackpack.desktopdata.db.relations.BookWithAuthors
import book.communication.Book as BookRemote
import com.intelligentbackpack.desktopdata.db.entities.Book as BookDB
import com.intelligentbackpack.desktopdomain.entities.Book as BookDomain

/**
 * Adapter for book
 */
internal object BookAdapter {

    /**
     * Convert from DB to domain
     */
    fun BookWithAuthors.fromDBToDomain(): BookDomain {
        return BookDomain.build {
            isbn = this@fromDBToDomain.book.isbn
            title = this@fromDBToDomain.book.title
            authors = this@fromDBToDomain.authors.map { it.fromDBToDomain() }.toSet()
        }
    }

    /**
     * Convert from domain to DB
     */
    fun BookDomain.fromDomainToDB(): BookDB =
        BookDB(
            isbn = this.isbn,
            title = this.title,
        )

    /**
     * Convert from remote to domain
     */
    fun BookRemote.fromRemoteToDomain(): BookDomain {
        return BookDomain.build {
            isbn = this@fromRemoteToDomain.isbn
            title = this@fromRemoteToDomain.titolo
            authors = setOf(this@fromRemoteToDomain.autore)
        }
    }
}
