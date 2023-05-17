package com.intelligentbackpack.desktopdata.datasource

import com.intelligentbackpack.desktopdomain.entities.Book
import com.intelligentbackpack.desktopdomain.entities.SchoolSupply

interface DesktopLocalDataSource {

    /**
     * Get all school supply from the local database
     */
    fun getAllSchoolSupplies(): Set<SchoolSupply>

    /**
     * Get a book from the local database
     *
     * @param isbn The ISBN of the book to get
     * @return The book with the given ISBN, or null if it doesn't exist
     */
    fun getBook(isbn: String): Book?

    /**
     * Add a book to the local database
     */
    fun addBook(book: Book): Book

    /**
     * Get a school supply from the local database
     *
     * @param rfid The RFID of the school supply to get
     * @return The school supply with the given RFID, or null if it doesn't exist
     */
    fun getSchoolSupply(rfid: String): SchoolSupply

    /**
     * Add a school supply to the local database
     *
     * @param schoolSupply The school supply to add
     */
    fun addSchoolSupply(schoolSupply: SchoolSupply)

    /**
     * Get all school supplies in the backpack
     */
    fun getSchoolSupplyInBackpack(): Set<SchoolSupply>

    /**
     * Put school supplies in the backpack only in the local database
     *
     * @param rfid the set of rfid of school supplies to put in the backpack
     */
    fun putSchoolSuppliesInBackpack(rfid: Set<String>)

    /**
     * Take school supplies from the backpack only in the local database
     *
     * @param rfid the set of rfid of school supplies to take from the backpack
     */
    fun takeSchoolSuppliesFromBackpack(rfid: Set<String>)

    /**
     * Get the backpack hash
     */
    fun getBackpack(): String?

    /**
     * Associate the backpack to the desktop
     *
     * @param hash the hash of the backpack
     */
    fun associateBackpack(hash: String)

    /**
     * Delete the desktop and all its school supplies
     */
    fun deleteDesktop()

    /**
     * Disassociate the backpack from the desktop
     *
     */
    fun disassociateBackpack()

    /**
     * Remove all school supplies from the backpack
     */
    fun removeAllSchoolSuppliesFromBackpack()
}