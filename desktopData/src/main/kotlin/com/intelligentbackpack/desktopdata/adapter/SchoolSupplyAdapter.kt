package com.intelligentbackpack.desktopdata.adapter

import com.intelligentbackpack.desktopdata.adapter.AuthorAdapter.fromDBToDomain
import com.intelligentbackpack.desktopdata.db.relations.BookCopyWithAuthors
import com.intelligentbackpack.desktopdomain.entities.Book
import com.intelligentbackpack.desktopdomain.entities.BookCopy
import book.communication.Copy as CopyRemote
import com.intelligentbackpack.desktopdata.db.entities.SchoolSupply as SchoolSupplyDB
import com.intelligentbackpack.desktopdomain.entities.BookCopy as BookCopyDomain

/**
 * Adapter for school supply
 */
internal object SchoolSupplyAdapter {

    /**
     * Convert from DB Book copy to domain Book copy
     */
    fun BookCopyWithAuthors.fromDBToDomain(): BookCopyDomain {
        return BookCopyDomain.build {
            this.rfidCode = this@fromDBToDomain.bookCopy.rfid
            this.book = Book.build {
                isbn = this@fromDBToDomain.bookCopy.isbn
                title = this@fromDBToDomain.bookCopy.title
                authors = this@fromDBToDomain.authors.map { it.fromDBToDomain() }.toSet()
            }
        }
    }

    /**
     * Convert from domain Book copy to DB Book copy
     */
    fun BookCopy.fromDomainToDB(type: Int, inBackpack: Boolean = false): SchoolSupplyDB =
        SchoolSupplyDB(
            rfid = this@fromDomainToDB.rfidCode,
            isbn = this@fromDomainToDB.book.isbn,
            type = type,
            inBackpack = inBackpack
        )

    /**
     * Convert from remote book copy to domain book copy
     *
     * @param books Set of books
     * @return Book copy
     * @throws IllegalArgumentException if book is not found
     */
    fun CopyRemote.fromRemoteToDomain(books: Set<Book>): BookCopyDomain =
        books.find { it.isbn == this@fromRemoteToDomain.isbn }?.let {
            return BookCopyDomain.build {
                this.rfidCode = this@fromRemoteToDomain.rfid
                this.book = it
            }
        } ?: throw IllegalArgumentException("Book not found")
}
