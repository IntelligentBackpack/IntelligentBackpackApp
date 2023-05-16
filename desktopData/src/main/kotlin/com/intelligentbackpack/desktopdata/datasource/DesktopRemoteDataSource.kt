package com.intelligentbackpack.desktopdata.datasource

import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.desktopdomain.entities.Book
import com.intelligentbackpack.desktopdomain.entities.Desktop
import com.intelligentbackpack.desktopdomain.entities.SchoolSupply
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the desktop remote data source.
 */
interface DesktopRemoteDataSource {
    /**
     * Gets a book by its ISBN.
     *
     * @param isbn The ISBN of the book.
     * @return The book if it exists.
     */
    fun getBook(isbn: String): Book?

    /**
     * Gets the desktop.
     *
     * @param user The user.
     * @return The desktop.
     */
    fun getDesktop(user: User): Desktop

    /**
     * Adds a school supply to the desktop.
     *
     * @param user The user.
     * @param schoolSupply The school supply to add.
     */
    fun addSchoolSupply(user: User, schoolSupply: SchoolSupply)

    /**
     * Subscribes to the backpack.
     *
     * @param user the user that owns the backpack
     * @param backpack the backpack to subscribe
     * @return a [Flow] with the set of rfid of school supplies in the backpack
     */
    fun subscribeToBackpackChanges(user: User, backpack: String): Flow<Result<Set<String>>>

    /**
     * Associates a backpack to a user.
     *
     * @param user the user to associate the backpack
     * @param hash the hash of the backpack to associate
     * @return the backpack hash
     */
    fun associateBackpack(user: User, hash: String) : String
}