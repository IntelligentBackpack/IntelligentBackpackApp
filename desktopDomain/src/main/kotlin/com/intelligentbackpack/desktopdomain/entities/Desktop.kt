package com.intelligentbackpack.desktopdomain.entities

import com.intelligentbackpack.desktopdomain.entities.implementations.DesktopImpl
import com.intelligentbackpack.desktopdomain.exception.BackpackAlreadyAssociatedException
import com.intelligentbackpack.desktopdomain.exception.DuplicateRFIDException
import com.intelligentbackpack.desktopdomain.exception.BackpackNotAssociatedException
import com.intelligentbackpack.desktopdomain.exception.TypeException

/**
 * Type alias for backpack.
 */
typealias Backpack = String

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
     * The backpack of the user.
     */
    val backpack: Backpack?

    /**
     * True if the backpack is connected for the user, false otherwise.
     */
    val isBackpackAssociated: Boolean
        get() = backpack != null

    /**
     * Adds a school supply to the desktop.
     *
     * @param schoolSupply The school supply to add.
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
    fun associateBackpack(backpack: Backpack)

    /**
     * Adds a school supply in the backpack.
     *
     * @param schoolSupply The school supply to add.
     */
    fun putSchoolSupplyInBackpack(schoolSupply: SchoolSupply)

    /**
     * Adds a set of school supplies in the backpack.
     *
     * @param schoolSupplies The school supplies to add.
     */
    fun putSchoolSuppliesInBackpack(schoolSupplies: Set<SchoolSupply>) =
        schoolSupplies.forEach { putSchoolSupplyInBackpack(it) }

    /**
     * Extract a school supplies from the backpack.
     *
     * @param schoolSupply The school supply to extract.
     */
    fun takeSchoolSupplyFromBackpack(schoolSupply: SchoolSupply)

    /**
     * Extract a set of school supply from the backpack.
     *
     * @param schoolSupplies The school supplies to extract.
     */
    fun takeSchoolSuppliesFromBackpack(schoolSupplies: Set<SchoolSupply>) =
        schoolSupplies.forEach { takeSchoolSupplyFromBackpack(it) }

    /**
     * Disassociates the backpack from the user.
     *
     * @param hash the hash of the backpack to disassociate
     */
    fun disassociateBackpack(hash: String)

    companion object {
        /**
         * Builds a desktop.
         *
         * @param schoolSupplies The school supplies of the desktop.
         * @param schoolSuppliesInBackpack The school supplies in the backpack.
         * @return The desktop built.
         */
        fun create(
            schoolSupplies: Set<SchoolSupply> = emptySet(),
            schoolSuppliesInBackpack: Set<SchoolSupply> = emptySet(),
            backpack: Backpack? = null,
        ): Desktop =
            if (backpack == null && schoolSuppliesInBackpack.isNotEmpty()) {
                throw BackpackNotAssociatedException()
            } else {
                DesktopImpl(
                    schoolSupplies,
                    setOf(SchoolSupplyTypes.BOOK),
                    schoolSuppliesInBackpack,
                    backpack,
                )
            }
    }
}
