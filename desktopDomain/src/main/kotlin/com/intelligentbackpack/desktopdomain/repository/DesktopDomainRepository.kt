package com.intelligentbackpack.desktopdomain.repository

import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.desktopdomain.entities.Book
import com.intelligentbackpack.desktopdomain.entities.Desktop
import com.intelligentbackpack.desktopdomain.entities.SchoolSupply
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the desktop domain repository.
 */
interface DesktopDomainRepository {

    /**
     * Downloads the desktop.
     *
     * @param user the user that wants the desktop
     * @return the desktop
     */

    suspend fun downloadDesktop(user: User): Desktop

    /**
     * Gets the desktop.
     *
     * @param user the user that wants the desktop
     * @return the desktop
     */
    suspend fun getDesktop(user: User): Desktop

    /**
     * Adds a school supply to the desktop.
     *
     * @param user The user that adds the school supply.
     * @param schoolSupply The school supply to add.
     * @return The set of school supplies.
     */
    suspend fun addSchoolSupply(
        user: User,
        schoolSupply: SchoolSupply,
    ): Set<SchoolSupply>

    /**
     * Gets a book by its ISBN.
     *
     * @param isbn The ISBN of the book.
     * @return The book or null if it doesn't exist.
     */
    suspend fun getBook(isbn: String): Book?

    /**
     * Deletes the desktop and all its school supplies.
     *
     * @param user The user that logout the desktop.
     */
    suspend fun logoutDesktop(user: User)

    /**
     * Subscribes to the backpack.
     *
     * @param user the user that subscribe to the backpack
     * @return a [Flow] of [Set] of [String] representing the rfid of the school supplies in the backpack
     */
    suspend fun subscribeToBackpack(user: User): Flow<Set<String>>

    /**
     * Associate the backpack to the desktop
     *
     * @param user the user that connect the backpack
     * @param hash the hash of the backpack to associate
     * @return the hash of the backpack
     */
    suspend fun associateBackpack(user: User, hash: String): String

    /**
     * Disassociate the backpack from the desktop
     *
     * @param user the user that disconnect the backpack
     * @param hash the hash of the backpack to disassociate
     * @return the hash of the backpack
     */
    suspend fun disassociateBackpack(user: User, hash: String): String

    /**
     * Deletes the desktop and all its school supplies.
     *
     * @param user The user that deletes the desktop.
     */
    suspend fun deleteDesktop(user: User)
}
