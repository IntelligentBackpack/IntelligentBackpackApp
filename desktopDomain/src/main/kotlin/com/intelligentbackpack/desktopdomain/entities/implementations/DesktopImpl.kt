package com.intelligentbackpack.desktopdomain.entities.implementations

import com.intelligentbackpack.desktopdomain.domainserivece.Replace
import com.intelligentbackpack.desktopdomain.entities.Backpack
import com.intelligentbackpack.desktopdomain.entities.Desktop
import com.intelligentbackpack.desktopdomain.entities.MutableSchoolSupply
import com.intelligentbackpack.desktopdomain.entities.SchoolSupply
import com.intelligentbackpack.desktopdomain.entities.SchoolSupplyType
import com.intelligentbackpack.desktopdomain.entities.Subject
import com.intelligentbackpack.desktopdomain.exception.AlreadyPresentType
import com.intelligentbackpack.desktopdomain.exception.DuplicateBackpack
import com.intelligentbackpack.desktopdomain.exception.DuplicateRFIDException
import com.intelligentbackpack.desktopdomain.exception.TypeException

/**
 * Implementation of a desktop.
 *
 * @property backpacks The backpacks of the desktop.
 * @property schoolSupplies The school supplies of the desktop.
 * @property schoolSupplyTypes The types of the school supplies of the desktop.
 */
internal data class DesktopImpl(
    override val backpacks: Set<Backpack>,
    override val schoolSupplies: Set<SchoolSupply>,
    override val schoolSupplyTypes: Set<SchoolSupplyType>,
    override val subjects: Set<Subject>
) : Desktop {

    override fun addBackpack(backpack: Backpack): Desktop =
        if (backpacks.contains(backpack))
            throw DuplicateBackpack()
        else
            DesktopImpl(
                backpacks = backpacks + backpack,
                schoolSupplies = schoolSupplies,
                schoolSupplyTypes = schoolSupplyTypes,
                subjects = subjects
            )

    override fun addSchoolSupply(schoolSupply: SchoolSupply): Desktop =
        if (!schoolSupplyTypes.contains(schoolSupply.type))
            throw TypeException(schoolSupply.type)
        else {
            if (schoolSupplies.map { it.rfidCode }.contains(schoolSupply.rfidCode))
                throw DuplicateRFIDException()
            else
                DesktopImpl(
                    backpacks = backpacks,
                    schoolSupplies = schoolSupplies + schoolSupply,
                    schoolSupplyTypes = schoolSupplyTypes,
                    subjects = subjects
                )
        }

    /**
     * Adds a school supply type to the desktop.
     *
     * @param schoolSupplyType The school supply type to add.
     * @return The desktop with the school supply type added.
     *
     * @throws AlreadyPresentType If the school supply type is already present.
     */
    @Throws(AlreadyPresentType::class)
    override fun addSchoolSupplyType(schoolSupplyType: SchoolSupplyType): Desktop =
        if (schoolSupplyTypes.contains(schoolSupplyType))
            throw AlreadyPresentType()
        else
            DesktopImpl(
                backpacks = backpacks,
                schoolSupplies = schoolSupplies,
                schoolSupplyTypes = schoolSupplyTypes + schoolSupplyType,
                subjects = subjects
            )

    override fun <T : MutableSchoolSupply<T>> replaceSchoolSupplies(
        newSchoolSupplies: Set<T>,
        oldSchoolSupplies: Set<T>
    ): Desktop {
        val replacement = Replace.replace(newSchoolSupplies, oldSchoolSupplies)
        return DesktopImpl(
            backpacks = backpacks,
            schoolSupplies = (
                schoolSupplies.filterNot { it in (oldSchoolSupplies + newSchoolSupplies) } +
                    replacement.newSchoolSupplies +
                    replacement.oldSchoolSupplies
                ).toSet(),
            schoolSupplyTypes = schoolSupplyTypes,
            subjects = subjects
        )
    }

    override fun addSubject(subject: Subject): Desktop {
        return DesktopImpl(
            backpacks = backpacks,
            schoolSupplies = schoolSupplies,
            schoolSupplyTypes = schoolSupplyTypes,
            subjects = subjects + subject
        )
    }
}
