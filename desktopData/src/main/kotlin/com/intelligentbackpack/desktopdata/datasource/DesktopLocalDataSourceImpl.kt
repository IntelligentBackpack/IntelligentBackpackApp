package com.intelligentbackpack.desktopdata.datasource

import com.intelligentbackpack.desktopdata.adapter.BookAdapter.fromDBToDomain
import com.intelligentbackpack.desktopdata.adapter.BookAdapter.fromDomainToDB
import com.intelligentbackpack.desktopdata.adapter.SchoolSupplyAdapter.fromDBToDomain
import com.intelligentbackpack.desktopdata.adapter.SchoolSupplyAdapter.fromDomainToDB
import com.intelligentbackpack.desktopdata.db.DesktopDatabase
import com.intelligentbackpack.desktopdata.db.entities.Author
import com.intelligentbackpack.desktopdata.db.entities.SchoolSupplyType
import com.intelligentbackpack.desktopdata.db.entities.Wrote
import com.intelligentbackpack.desktopdata.storage.DesktopStorage
import com.intelligentbackpack.desktopdomain.entities.Book
import com.intelligentbackpack.desktopdomain.entities.BookCopy
import com.intelligentbackpack.desktopdomain.entities.SchoolSupply
import com.intelligentbackpack.desktopdomain.entities.SchoolSupplyTypes
import com.intelligentbackpack.desktopdomain.exception.SchoolSupplyNotFoundException
import com.intelligentbackpack.desktopdomain.exception.TypeException

class DesktopLocalDataSourceImpl(
    private val database: DesktopDatabase,
    private val storage: DesktopStorage,
) : DesktopLocalDataSource {

    override fun getAllSchoolSupplies(): Set<SchoolSupply> =
        database.desktopDao()
            .getAllSchoolSupplyRfid()
            .map { getSchoolSupply(it) }
            .toSet()

    override fun getBook(isbn: String): Book? =
        database.desktopDao().getBook(isbn)?.fromDBToDomain()

    override fun addBook(book: Book): Book {
        val authorsDB = book.authors
            .map {
                database.desktopDao().getAuthors(it).getOrNull(0)
                    ?: database.desktopDao().addAuthor(Author(0, it))
                        .let { authorId ->
                            database.desktopDao().getAuthor(authorId)
                        }
            }
        database.desktopDao().addBook(book.fromDomainToDB())
        authorsDB.forEach { author ->
            database.desktopDao().addWrote(Wrote(book.isbn, author.authorId))
        }
        return book
    }

    override fun getSchoolSupply(rfid: String): SchoolSupply =
        database.desktopDao().getSchoolSupplyType(rfid)?.let {
            when (it) {
                SchoolSupplyTypes.BOOK -> {
                    database.desktopDao().getBookCopy(rfid)
                        ?.fromDBToDomain()
                }

                else -> {
                    throw TypeException(it)
                }
            }
        } ?: run {
            throw SchoolSupplyNotFoundException(rfid)
        }

    override fun addSchoolSupply(schoolSupply: SchoolSupply) {
        val type = database.desktopDao().getSchoolSupplyTypesFromName(schoolSupply.type).first().typeId
        when (schoolSupply) {
            is BookCopy -> {
                if (database.desktopDao().getBook(schoolSupply.book.isbn) == null) {
                    val authors = schoolSupply.book.authors.map { author ->
                        database.desktopDao().addAuthor(Author(0, author))
                    }
                    database.desktopDao().addBook(schoolSupply.book.fromDomainToDB())
                    authors.forEach { author ->
                        database.desktopDao().addWrote(Wrote(schoolSupply.book.isbn, author.toInt()))
                    }
                }
                database.desktopDao().addSchoolSupply(schoolSupply.fromDomainToDB(type))
            }

            else -> {
                throw TypeException(schoolSupply.type)
            }
        }
    }

    override fun getSchoolSupplyInBackpack(): Set<SchoolSupply> =
        database.desktopDao().getBookCopiesInBackpack().map { it.fromDBToDomain() }.toSet()

    override fun putSchoolSuppliesInBackpack(rfid: Set<String>) {
        database.desktopDao().putSchoolSuppliesInBackpack(rfid)
    }

    override fun takeSchoolSuppliesFromBackpack(rfid: Set<String>) {
        database.desktopDao().takeSchoolSuppliesFromBackpack(rfid)
    }

    override fun getBackpack(): String? =
        if (storage.isBackpackSaved()) {
            storage.getBackpack()
        } else {
            null
        }

    override fun associateBackpack(hash: String) {
        storage.saveBackpack(hash)
    }

    override fun deleteDesktop() {
        database.clearAllTables()
        storage.deleteBackpack()
        database.desktopDao().insertSchoolSupplyType(SchoolSupplyType(0, SchoolSupplyTypes.BOOK))
    }

    override fun disassociateBackpack() {
        storage.deleteBackpack()
    }

    override fun removeAllSchoolSuppliesFromBackpack() {
        database.desktopDao().removeAllSchoolSuppliesFromBackpack()
    }
}
