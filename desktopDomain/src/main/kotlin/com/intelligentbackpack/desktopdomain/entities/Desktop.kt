package com.intelligentbackpack.desktopdomain.entities

import com.intelligentbackpack.desktopdomain.entities.implementations.DesktopImpl
import com.intelligentbackpack.desktopdomain.exception.BackpackAlreadyAssociatedException
import com.intelligentbackpack.desktopdomain.exception.DuplicateRFIDException
import com.intelligentbackpack.desktopdomain.exception.TypeException

/**
 * Interface for the desktop of the user.
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
     * The school supplies in the backpack.
     */
    val schoolSuppliesInBackpack: Set<SchoolSupply>

    /**
     * True if the backpack is connected for the user, false otherwise.
     */
    val backpackAssociated: Boolean

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
    fun addSchoolSupply(schoolSupply: SchoolSupply)

    /**
     * Connects the backpack for the desktop's user.
     *
     * @throws BackpackAlreadyAssociatedException If a backpack is already connected.
     */
    @Throws(BackpackAlreadyAssociatedException::class)
    fun associateBackpack()

    /**
     * Adds a school supply in the backpack.
     *
     * @param schoolSupply The school supply to add.
     * @return The desktop with the school supply added.
     */
    fun putSchoolSupplyInBackpack(schoolSupply: SchoolSupply)

    /**
     * Adds a set of school supplies in the backpack.
     *
     * @param schoolSupplies The school supplies to add.
     * @return The desktop with the school supply added.
     */
    fun putSchoolSuppliesInBackpack(schoolSupplies: Set<SchoolSupply>) =
        schoolSupplies.forEach { putSchoolSupplyInBackpack(it) }

    /**
     * Extract a school supplies from the backpack.
     *
     * @param schoolSupply The school supply to extract.
     * @return The desktop with the school supply removed.
     */
    fun takeSchoolSupplyFromBackpack(schoolSupply: SchoolSupply)

    /**
     * Extract a set of school supply from the backpack.
     *
     * @param schoolSupplies The school supplies to extract.
     * @return The desktop with the school supply removed.
     */
    fun takeSchoolSuppliesFromBackpack(schoolSupplies: Set<SchoolSupply>) =
        schoolSupplies.forEach { takeSchoolSupplyFromBackpack(it) }

    companion object {
        /**
         * Builds a desktop.
         *
         * @param schoolSupplies The school supplies of the desktop.
         * @param schoolSuppliesInBackpack The school supplies in the backpack.
         * @param backpackAssociated true if the backpack is associated for the user, false otherwise.
         * @return The desktop built.
         */
        fun create(
            schoolSupplies: Set<SchoolSupply> = emptySet(),
            schoolSuppliesInBackpack: Set<SchoolSupply> = emptySet(),
            backpackAssociated: Boolean = false
        ): Desktop =
            DesktopImpl(
                schoolSupplies,
                setOf(SchoolSupplyTypes.BOOK),
                schoolSuppliesInBackpack,
                backpackAssociated
            )
    }
}
