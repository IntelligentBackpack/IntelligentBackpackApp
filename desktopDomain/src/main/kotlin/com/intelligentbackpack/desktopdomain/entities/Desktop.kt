package com.intelligentbackpack.desktopdomain.entities

import com.intelligentbackpack.desktopdomain.entities.implementations.DesktopImpl
import com.intelligentbackpack.desktopdomain.exception.AlreadyPresentType
import com.intelligentbackpack.desktopdomain.exception.DuplicateBackpack
import com.intelligentbackpack.desktopdomain.exception.DuplicateRFIDException
import com.intelligentbackpack.desktopdomain.exception.ReplaceException
import com.intelligentbackpack.desktopdomain.exception.TypeException

/**
 * Interface for a desktop.
 */
interface Desktop {
    /**
     * The backpacks of the desktop.
     */
    val backpacks: Set<Backpack>

    /**
     * The school supplies of the desktop.
     */
    val schoolSupplies: Set<SchoolSupply>

    /**
     * The types of the school supplies of the desktop.
     */
    val schoolSupplyTypes: Set<SchoolSupplyType>

    /**
     * The subjects of the school supplies of the desktop.
     */
    val subjects: Set<Subject>

    /**
     * Adds a backpack to the desktop.
     *
     * @param backpack The backpack to add.
     * @return The desktop with the backpack added.
     *
     * @throws DuplicateBackpack If the backpack is already present.
     */
    @Throws(DuplicateBackpack::class)
    fun addBackpack(backpack: Backpack): Desktop

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

    /**
     * Adds a school supply type to the desktop.
     *
     * @param schoolSupplyType The school supply type to add.
     * @return The desktop with the school supply type added.
     *
     * @throws AlreadyPresentType If the school supply type is already present.
     */
    @Throws(AlreadyPresentType::class)
    fun addSchoolSupplyType(schoolSupplyType: SchoolSupplyType): Desktop

    /**
     * Adds a subject to the desktop.
     *
     * @param subject The subject to add.
     * @return The desktop with the subject added.
     */
    fun addSubject(subject: Subject): Desktop

    /**
     * The school supplies that replace the given school supplies.
     * Replaced means that the given school supplies are replaced as default material by the returned school supplies.
     *
     * @param T The type of the school supplies.
     * @param newSchoolSupplies The new school supplies.
     * @param oldSchoolSupplies The old school supplies that are replaced.
     * @return The school supplies that replace the given school supplies.
     * @throws ReplaceException If the old school supplies can not be replace the new ones.
     */
    @Throws(ReplaceException::class)
    fun <T : MutableSchoolSupply<T>> replaceSchoolSupplies(
        newSchoolSupplies: Set<T>,
        oldSchoolSupplies: Set<T>
    ): Desktop

    companion object {
        /**
         * Builds a desktop.
         *
         * @param backpacks The backpacks of the desktop.
         * @param schoolSupplies The school supplies of the desktop.
         * @param schoolSupplyTypes The types of the school supplies of the desktop.
         * @return The desktop built.
         */
        fun create(
            backpacks: Set<Backpack>,
            schoolSupplies: Set<SchoolSupply>,
            schoolSupplyTypes: Set<SchoolSupplyType>,
            subjects: Set<Subject>
        ): Desktop =
            DesktopImpl(
                backpacks,
                schoolSupplies,
                schoolSupplyTypes + SchoolSupplyTypes.BOOK,
                (subjects + Subjects.ALL).toSet()
            )
    }
}
