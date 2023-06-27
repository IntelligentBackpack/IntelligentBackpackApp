package com.intelligentbackpack.desktopdomain.entities

import com.intelligentbackpack.desktopdomain.entities.implementations.DesktopImpl
import com.intelligentbackpack.desktopdomain.exception.BackpackAlreadyAssociatedException
import com.intelligentbackpack.desktopdomain.exception.BackpackNotAssociatedException
import com.intelligentbackpack.desktopdomain.exception.DuplicateRFIDException
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
     * @return The desktop with the school supply added.
     * @throws TypeException If the school supply type is not present.
     * @throws DuplicateRFIDException If the school supply RFID code is already present.
     */
    @Throws(TypeException::class, DuplicateRFIDException::class)
    fun addSchoolSupply(schoolSupply: SchoolSupply): Desktop

    /**
     * Connects the backpack for the desktop's user.
     *
     * @param backpack The backpack to connect.
     * @return The desktop with the backpack connected.
     * @throws BackpackAlreadyAssociatedException If a backpack is already connected.
     */
    @Throws(BackpackAlreadyAssociatedException::class)
    fun associateBackpack(backpack: Backpack): Desktop

    /**
     * Adds a school supply in the backpack.
     *
     * @param schoolSupply The school supply to add.
     * @return The desktop with the school supply added in the backpack.
     */
    fun putSchoolSupplyInBackpack(schoolSupply: SchoolSupply): Desktop

    /**
     * Adds a set of school supplies in the backpack.
     *
     * @param schoolSupplies The school supplies to add.
     * @return The desktop with the school supplies added in the backpack.
     */
    fun putSchoolSuppliesInBackpack(schoolSupplies: Set<SchoolSupply>): Desktop =
        schoolSupplies.fold(this) { desktop, schoolSupply -> desktop.putSchoolSupplyInBackpack(schoolSupply) }

    /**
     * Extract a school supplies from the backpack.
     *
     * @param schoolSupply The school supply to extract.
     * @return The desktop with the school supply extracted from the backpack.
     */
    fun takeSchoolSupplyFromBackpack(schoolSupply: SchoolSupply): Desktop

    /**
     * Extract a set of school supply from the backpack.
     *
     * @param schoolSupplies The school supplies to extract.
     * @return The desktop with the school supplies extracted from the backpack.
     */
    fun takeSchoolSuppliesFromBackpack(schoolSupplies: Set<SchoolSupply>): Desktop =
        schoolSupplies.fold(this) { desktop, schoolSupply -> desktop.takeSchoolSupplyFromBackpack(schoolSupply) }

    /**
     * Disassociates the backpack from the user.
     *
     * @param hash the hash of the backpack to disassociate
     * @return The desktop with the backpack disassociated.
     */
    fun disassociateBackpack(hash: String): Desktop

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
