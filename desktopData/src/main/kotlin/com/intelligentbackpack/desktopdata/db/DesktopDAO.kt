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
import com.intelligentbackpack.desktopdomain.entities.SchoolSupplyTypes

/**
 * Desktop DAO to access database
 */
@Dao
internal interface DesktopDAO {

    @Query("INSERT INTO School_supply_types (type_id, type) VALUES (0, :defaultType)")
    fun insertDefaultType(defaultType: String = SchoolSupplyTypes.BOOK)

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

    /**
     * Add a wrote relation to the database
     *
     * @param wrote the wrote relation to add
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addWrote(wrote: Wrote)

    /**
     * Change the field in_backpack of a school supply to true
     *
     * @param rfid the rfid of the school supplies to put in the backpack
     */
    @Query("UPDATE school_supplies SET in_backpack = 1 WHERE rfid IN (:rfid)")
    fun putSchoolSuppliesInBackpack(rfid: Set<String>)

    /**
     * Change the field in_backpack of a school supply to false
     *
     * @param rfid the rfid of the school supplies to take from the backpack
     */
    @Query("UPDATE school_supplies SET in_backpack = 0 WHERE rfid IN (:rfid)")
    fun takeSchoolSuppliesFromBackpack(rfid: Set<String>)

    /**
     * Get all the authors
     *
     * @return the authors
     */
    @Query("SELECT * FROM Authors WHERE name = :name")
    fun getAuthors(name: String): List<Author>

    /**
     * Get an author from its id
     *
     * @param authorId the author id
     * @return the author
     */
    @Query("SELECT * FROM Authors WHERE author_id = :authorId")
    fun getAuthor(authorId: Long): Author

    /**
     * Get all school supply types
     *
     * @return the school supply types
     */
    @Query("SELECT * FROM School_supply_types")
    fun getSchoolSupplyTypes(): List<SchoolSupplyType>

    /**
     * Get a school supply type from its name
     *
     * @param type the school supply type name
     * @return the school supply type
     */
    @Query("SELECT * FROM School_supply_types WHERE type = :type")
    fun getSchoolSupplyTypesFromName(type: String): List<SchoolSupplyType>

    /**
     * Get all books
     *
     * @return the books
     */
    @Transaction
    @Query("SELECT * FROM Books")
    fun getBooks(): List<BookWithAuthors>

    /**
     * Get a book from its isbn
     *
     * @param isbn the book isbn
     * @return the book
     */
    @Transaction
    @Query("SELECT * FROM Books WHERE isbn = :isbn")
    fun getBook(isbn: String): BookWithAuthors?

    /**
     * Get all book copy from the rfid
     *
     * @param rfid the book copy rfid
     * @return the book copy
     */
    @Transaction
    @Query("SELECT * FROM Book_copies WHERE rfid = :rfid")
    fun getBookCopy(rfid: String): BookCopyWithAuthors?

    /**
     * Get all book copies in the backpack
     *
     * @return the book copies in the backpack
     */
    @Transaction
    @Query("SELECT * FROM Book_copies WHERE in_backpack = 1")
    fun getBookCopiesInBackpack(): List<BookCopyWithAuthors>

    /**
     * Get the school supply type from the rfid
     *
     * @param rfid the school supply
     * @return the school supply type
     */
    @Transaction
    @Query(
        "SELECT School_supply_types.type" +
                " FROM School_supplies INNER JOIN School_supply_types ON  School_supplies.type = School_supply_types.type_id " +
                " WHERE rfid = :rfid"
    )
    fun getSchoolSupplyType(rfid: String): String?

    /**
     * Get all school supplies' rfid
     *
     * @return the school supplies' rfid
     */
    @Transaction
    @Query("SELECT rfid FROM School_supplies")
    fun getAllSchoolSupplyRfid(): List<String>

}
