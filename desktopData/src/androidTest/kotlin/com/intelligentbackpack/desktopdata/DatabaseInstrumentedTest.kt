package com.intelligentbackpack.desktopdata

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.intelligentbackpack.desktopdata.db.DesktopDatabase
import com.intelligentbackpack.desktopdata.db.DesktopDatabaseHelper
import com.intelligentbackpack.desktopdata.db.entities.Author
import com.intelligentbackpack.desktopdata.db.entities.Book
import com.intelligentbackpack.desktopdata.db.entities.SchoolSupply
import com.intelligentbackpack.desktopdata.db.entities.Wrote
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 */
@RunWith(AndroidJUnit4::class)
class DatabaseInstrumentedTest {
    private val author1 = Author(
        authorId = 1,
        name = "Author 1"
    )
    private val author2 = Author(
        authorId = 2,
        name = "Author 2"
    )
    private val book = Book(
        isbn = "1234567890",
        title = "The Book"
    )
    private val wrote1 = Wrote(
        isbn = book.isbn,
        authorId = author1.authorId
    )
    private val wrote2 = Wrote(
        isbn = book.isbn,
        authorId = author2.authorId
    )
    private val schoolSupply = SchoolSupply(
        rfid = "FF:FF:FF:FF",
        type = 0,
        isbn = book.isbn,
        inBackpack = false
    )

    private fun insertBook(db: DesktopDatabase) {
        db.desktopDao().addAuthor(author1)
        db.desktopDao().addAuthor(author2)
        db.desktopDao().addBook(book)
        db.desktopDao().addWrote(wrote1)
        db.desktopDao().addWrote(wrote2)
    }

    private fun insertSchoolSupply(db: DesktopDatabase) {
        insertBook(db)
        db.desktopDao().addSchoolSupply(schoolSupply)
    }

    @Test
    fun createBasicDatabase() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val db = DesktopDatabaseHelper.getDatabase(appContext)
        val schoolSupplyTypes = db.desktopDao().getSchoolSupplyTypes()
        assertEquals(1, schoolSupplyTypes.size)
        assertEquals("Book", schoolSupplyTypes[0].type)
        assertEquals(0, schoolSupplyTypes[0].typeId)
    }

    @Test
    fun bookBasicOperation() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val db = DesktopDatabaseHelper.getDatabase(appContext)
        insertBook(db)
        val books = db.desktopDao().getBooks()
        assertEquals(1, books.size)
        assertEquals(2, books[0].authors.size)
        assertEquals(book.isbn, books[0].book.isbn)
    }

    @Test
    fun schoolSupplyBasicOperation() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val db = DesktopDatabaseHelper.getDatabase(appContext)
        insertSchoolSupply(db)
        val schoolSupplyQuery = db.desktopDao().getBookCopy(schoolSupply.rfid)
        assertEquals(1, schoolSupplyQuery.size)
        assertEquals(2, schoolSupplyQuery[0].authors.size)
        assertEquals(book.isbn, schoolSupplyQuery[0].bookCopy.isbn)
        assertEquals(schoolSupply.rfid, schoolSupplyQuery[0].bookCopy.rfid)
    }

    @Test
    fun getABookNotInDatabase() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val db = DesktopDatabaseHelper.getDatabase(appContext)
        insertBook(db)
        val book = db.desktopDao().getBook("1234567891")
        assertNull(book)
    }

    @Test
    fun checkAutoIncrement() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val db = DesktopDatabaseHelper.getDatabase(appContext)
        insertBook(db)
        val author = Author(
            authorId = 0,
            name = "Author 3"
        )
        val authorId = db.desktopDao().addAuthor(author)
        val authorFromDB = db.desktopDao().getAuthor(authorId)
        assertEquals(3, authorFromDB.authorId)
    }

    @Test
    fun putSchoolSuppliesInBackpack() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val db = DesktopDatabaseHelper.getDatabase(appContext)
        insertSchoolSupply(db)
        db.desktopDao().putSchoolSuppliesInBackpack(setOf(schoolSupply.rfid))
        val bookCopy = db.desktopDao().getBookCopy(schoolSupply.rfid)
        assert(bookCopy.first().bookCopy.inBackpack)
    }

    @Test
    fun takeSchoolSuppliesFromBackpack() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val db = DesktopDatabaseHelper.getDatabase(appContext)
        insertSchoolSupply(db)
        db.desktopDao().putSchoolSuppliesInBackpack(setOf(schoolSupply.rfid))
        val bookCopy = db.desktopDao().getBookCopy(schoolSupply.rfid)
        assert(bookCopy.first().bookCopy.inBackpack)
        db.desktopDao().takeSchoolSuppliesFromBackpack(setOf(schoolSupply.rfid))
        val bookCopy2 = db.desktopDao().getBookCopy(schoolSupply.rfid)
        assert(!bookCopy2.first().bookCopy.inBackpack)
    }
}
