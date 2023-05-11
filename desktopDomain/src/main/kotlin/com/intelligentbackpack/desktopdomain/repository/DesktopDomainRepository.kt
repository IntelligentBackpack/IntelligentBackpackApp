package com.intelligentbackpack.desktopdomain.repository

import com.intelligentbackpack.desktopdomain.entities.Desktop
import com.intelligentbackpack.desktopdomain.entities.SchoolSupply

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
    fun getDesktop(success: (Desktop) -> Unit, error: (Exception) -> Unit)

    /**
     * Adds a school supply to the desktop.
     *
     * @param schoolSupply The school supply to add.
     * @param success The success callback with all the school supplies.
     * @param error The error callback.
     */
    fun addSchoolSupply(schoolSupply: SchoolSupply, success: (Set<SchoolSupply>) -> Unit, error: (Exception) -> Unit)

    /**
     * Gets a school supply by its RFID.
     *
     * @param rfid The RFID of the school supply.
     * @param success The success callback with the school supply.
     * @param error The error callback.
     */
    fun getSchoolSupply(rfid: String, success: (SchoolSupply) -> Unit, error: (Exception) -> Unit)

    /**
     * Deletes the desktop and all its school supplies.
     *
     * @param success The success callback.
     * @param error The error callback.
     */
    fun deleteDesktop(success: () -> Unit, error: (Exception) -> Unit)
}
