package com.intelligentbackpack.desktopdata.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.intelligentbackpack.desktopdata.db.entities.Author
import com.intelligentbackpack.desktopdata.db.entities.Book
import com.intelligentbackpack.desktopdata.db.entities.SchoolSupply
import com.intelligentbackpack.desktopdata.db.entities.SchoolSupplyType
import com.intelligentbackpack.desktopdata.db.entities.Wrote
import com.intelligentbackpack.desktopdata.db.relations.BookCopyWithAuthors
import com.intelligentbackpack.desktopdata.db.relations.BookWithAuthors

/**
 * Desktop DAO to access database
 */
@Dao
internal interface DesktopDAO {

    /**
     * Get an author id from the author name
     *
     * @param author author name
     * @return author id
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAuthor(author: Author): Long

    /**
     * Get an authors' id from their names
     *
     * @param authors authors' name
     * @return author ids
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAuthors(authors: List<Author>): List<Long>

    /**
     * Get a book from the book isbn
     *
     * @param book book isbn
     * @return the book
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addBook(book: Book)

    /**
     * Add a school supply to the database
     *
     * @param schoolSupply the school supply to add
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun addSchoolSupply(schoolSupply: SchoolSupply)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addWrote(wrote: Wrote)

    @Query("UPDATE school_supplies SET in_backpack = 1 WHERE rfid IN (:rfid)")
    fun putSchoolSuppliesInBackpack(rfid: Set<String>)

    @Query("UPDATE school_supplies SET in_backpack = 0 WHERE rfid IN (:rfid)")
    fun takeSchoolSuppliesFromBackpack(rfid: Set<String>)

    @Query("SELECT * FROM Authors WHERE name = :name")
    fun getAuthors(name: String): List<Author>

    @Query("SELECT * FROM Authors WHERE author_id = :authorId")
    fun getAuthor(authorId: Long): Author

    @Query("SELECT * FROM School_supply_types")
    fun getSchoolSupplyTypes(): List<SchoolSupplyType>

    @Query("SELECT * FROM School_supply_types WHERE type = :type")
    fun getSchoolSupplyTypesFromName(type: String): List<SchoolSupplyType>

    @Transaction
    @Query("SELECT * FROM Books")
    fun getBooks(): List<BookWithAuthors>

    @Transaction
    @Query("SELECT * FROM Books WHERE isbn = :isbn")
    fun getBook(isbn: String): BookWithAuthors?

    @Transaction
    @Query("SELECT * FROM Book_copies WHERE rfid = :rfid")
    fun getBookCopy(rfid: String): List<BookCopyWithAuthors>

    @Transaction
    @Query("SELECT * FROM Book_copies WHERE in_backpack = 1")
    fun getBookCopiesInBackpack(): List<BookCopyWithAuthors>

    @Transaction
    @Query(
        "SELECT School_supply_types.type" +
                " FROM School_supplies INNER JOIN School_supply_types ON  School_supplies.type = School_supply_types.type_id " +
                " WHERE rfid = :rfid"
    )
    fun getSchoolSupplyType(rfid: String): String?

    @Transaction
    @Query("SELECT rfid FROM School_supplies")
    fun getAllSchoolSupplyRfid(): List<String>

}
