package com.intelligentbackpack.desktopdomain.entities

import com.intelligentbackpack.desktopdomain.entities.implementations.DesktopImpl
import com.intelligentbackpack.desktopdomain.exception.DuplicateRFIDException
import com.intelligentbackpack.desktopdomain.exception.TypeException

/**
 * Interface for a desktop.
 */
interface Desktop {

    /**
     * The school supplies of the desktop.
     */
    val schoolSupplies: Set<SchoolSupply>

    /**
     * The types of the school supplies of the desktop.
     */
    val schoolSupplyTypes: Set<SchoolSupplyType>

    /**
     * Adds a school supply to the desktop.
     *
     * @param schoolSupply The school supply to add.
     * @return The desktop with the school supply added.
     *
     * @throws TypeException If the school supply type is not present.
     * @throws DuplicateRFIDException If the school supply RFID code is already present.
     */
    @Throws(TypeException::class, DuplicateRFIDException::class)
    fun addSchoolSupply(schoolSupply: SchoolSupply): Desktop

    companion object {
        /**
         * Builds a desktop.
         *
         * @param schoolSupplies The school supplies of the desktop.
         * @return The desktop built.
         */
        fun create(
            schoolSupplies: Set<SchoolSupply> = emptySet()
        ): Desktop =
            DesktopImpl(
                schoolSupplies,
                setOf(SchoolSupplyTypes.BOOK),
            )
    }
}
