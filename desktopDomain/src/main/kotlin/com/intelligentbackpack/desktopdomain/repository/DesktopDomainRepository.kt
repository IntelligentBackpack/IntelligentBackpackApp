package com.intelligentbackpack.desktopdomain.repository

import com.intelligentbackpack.desktopdomain.entities.Book
import com.intelligentbackpack.desktopdomain.entities.Desktop
import com.intelligentbackpack.desktopdomain.entities.SchoolSupply
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the desktop domain repository.
 */
interface DesktopDomainRepository {

    /**
     * Gets the desktop.
     *
     * @param success The success callback.
     * @param error The error callback.
     */
    suspend fun getDesktop(success: (Desktop) -> Unit, error: (Exception) -> Unit)

    /**
     * Adds a school supply to the desktop.
     *
     * @param schoolSupply The school supply to add.
     * @param success The success callback with all the school supplies.
     * @param error The error callback.
     */
    suspend fun addSchoolSupply(
        schoolSupply: SchoolSupply,
        success: (Set<SchoolSupply>) -> Unit,
        error: (Exception) -> Unit
    )

    /**
     * Gets a book by its ISBN.
     *
     * @param isbn The ISBN of the book.
     * @param success The success callback with the book if it exists.
     * @param error The error callback.
     */
    suspend fun getBook(isbn: String, success: (Book?) -> Unit, error: (Exception) -> Unit)

    /**
     * Gets a school supply by its RFID.
     *
     * @param rfid The RFID of the school supply.
     * @param success The success callback with the school supply.
     * @param error The error callback.
     */
    suspend fun getSchoolSupply(rfid: String, success: (SchoolSupply) -> Unit, error: (Exception) -> Unit)

    /**
     * Deletes the desktop and all its school supplies.
     *
     * @param success The success callback.
     * @param error The error callback.
     */
    suspend fun deleteDesktop(success: () -> Unit, error: (Exception) -> Unit)

    /**
     * Subscribes to the backpack.
     *
     * @param success The success callback with a [Flow] of the rfid of school supplies in backpack
     * @param error The error callback.
     */
    suspend fun subscribeToBackpack(success: (Flow<Set<String>>) -> Unit, error: (Exception) -> Unit)

    /**
     * Put a set of school supplies in the backpack
     *
     * @param rfid the set of rfid of school supplies to put in the backpack
     */
    suspend fun putSchoolSuppliesInBackpack(rfid: Set<String>)

    /**
     * Take a set of school supplies from the backpack
     *
     * @param rfid the set of rfid of school supplies to take from the backpack
     */
    suspend fun takeSchoolSuppliesFromBackpack(rfid: Set<String>)
}
