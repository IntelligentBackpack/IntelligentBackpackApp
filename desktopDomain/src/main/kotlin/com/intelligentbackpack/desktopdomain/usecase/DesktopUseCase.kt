package com.intelligentbackpack.desktopdomain.usecase

import com.intelligentbackpack.desktopdomain.entities.Book
import com.intelligentbackpack.desktopdomain.entities.Desktop
import com.intelligentbackpack.desktopdomain.entities.SchoolSupply
import com.intelligentbackpack.desktopdomain.repository.DesktopDomainRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for the desktop domain.
 */
class DesktopUseCase(private val repository: DesktopDomainRepository) {
    /**
     * Gets the desktop.
     *
     * @param success the success callback with the desktop.
     * @param error the error callback.
     */
    suspend fun getDesktop(success: (Desktop) -> Unit, error: (Exception) -> Unit) =
        repository.getDesktop(success, error)

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
    ) =
        repository.addSchoolSupply(schoolSupply, success, error)

    suspend fun getBook(isbn: String, success: (Book?) -> Unit, error: (Exception) -> Unit) =
        repository.getBook(isbn, success, error)

    /**
     * Gets the school supply given the rfid.
     *
     * @param rfid The rfid.
     * @param success The success callback with the school supply.
     * @param error The error callback.
     */
    suspend fun getSchoolSupply(rfid: String, success: (SchoolSupply) -> Unit, error: (Exception) -> Unit) =
        repository.getSchoolSupply(rfid, success, error)

    /**
     * Puts a school supply in the backpack.
     *
     * @param rfid The RFID of the school supply.
     * @param success The success callback with the new desktop.
     * @param error The error callback.
     */
    suspend fun putSchoolSupplyInBackpack(rfid: String, success: (Desktop) -> Unit, error: (Exception) -> Unit) =
        repository.putSchoolSupplyInBackpack(rfid, success, error)

    /**
     * Takes a school supply from the backpack.
     *
     * @param rfid The RFID of the school supply.
     * @param success The success callback with the new desktop.
     * @param error The error callback.
     */
    suspend fun takeSchoolSupplyFromBackpack(rfid: String, success: (Desktop) -> Unit, error: (Exception) -> Unit) =
        repository.takeSchoolSupplyFromBackpack(rfid, success, error)

    /**
     * Subscribes to the backpack.
     *
     * @param success The success callback with a [Flow] of the school supplies in backpack
     * @param error The error callback.
     */
    suspend fun subscribeToBackpack(success: (Flow<Set<SchoolSupply>>) -> Unit, error: (Exception) -> Unit) =
        repository.subscribeToBackpack(success, error)

    /**
     * Delete the desktop.
     *
     * @param success The success callback.
     * @param error The error callback.
     */
    suspend

    fun deleteDesktop(success: () -> Unit, error: (Exception) -> Unit) =
        repository.deleteDesktop(success, error)
}
