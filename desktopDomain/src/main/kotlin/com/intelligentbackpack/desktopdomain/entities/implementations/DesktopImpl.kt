package com.intelligentbackpack.desktopdomain.entities.implementations

import com.intelligentbackpack.desktopdomain.entities.Desktop
import com.intelligentbackpack.desktopdomain.entities.SchoolSupply
import com.intelligentbackpack.desktopdomain.entities.SchoolSupplyType
import com.intelligentbackpack.desktopdomain.exception.AlreadyInBackpackException
import com.intelligentbackpack.desktopdomain.exception.DuplicateRFIDException
import com.intelligentbackpack.desktopdomain.exception.SchoolSupplyNotFoundException
import com.intelligentbackpack.desktopdomain.exception.TypeException
import kotlin.jvm.Throws

/**
 * Implementation of a desktop.
 *
 * @property schoolSupplies The school supplies of the desktop.
 * @property schoolSupplyTypes The types of the school supplies of the desktop.
 */
internal data class DesktopImpl(
    override val schoolSupplies: Set<SchoolSupply>,
    override val schoolSupplyTypes: Set<SchoolSupplyType>,
    override val schoolSuppliesInBackpack: Set<SchoolSupply> = emptySet()
) : Desktop {

    /**
     * Add a school supply to the desktop.
     *
     * @param schoolSupply The school supply to add.
     * @return A new desktop with the added school supply.
     * @throws TypeException If the type of the school supply is not in the desktop.
     * @throws DuplicateRFIDException If the RFID code of the school supply is already in the desktop.
     */
    override fun addSchoolSupply(schoolSupply: SchoolSupply): Desktop =
        if (!schoolSupplyTypes.contains(schoolSupply.type))
            throw TypeException(schoolSupply.type)
        else {
            if (schoolSupplies.map { it.rfidCode }.contains(schoolSupply.rfidCode))
                throw DuplicateRFIDException()
            else
                DesktopImpl(
                    schoolSupplies = schoolSupplies + schoolSupply,
                    schoolSupplyTypes = schoolSupplyTypes,
                )
        }

    /**
     * Put a school supply in the backpack.
     *
     * @param schoolSupply The school supply to put in the backpack.
     * @return A new desktop with the school supply in the backpack.
     * @throws SchoolSupplyNotFoundException If the school supply is not in the desktop.
     * @throws AlreadyInBackpackException If the school supply is already in the backpack.
     */
    @Throws(SchoolSupplyNotFoundException::class)
    override fun putSchoolSupplyInBackpack(schoolSupply: SchoolSupply): Desktop {
        if (!schoolSupplies.contains(schoolSupply))
            throw SchoolSupplyNotFoundException(schoolSupply.rfidCode)
        else {
            if (schoolSuppliesInBackpack.contains(schoolSupply))
                throw AlreadyInBackpackException(schoolSupply)
            else
                return DesktopImpl(
                    schoolSupplies = schoolSupplies,
                    schoolSupplyTypes = schoolSupplyTypes,
                    schoolSuppliesInBackpack = schoolSuppliesInBackpack + schoolSupply
                )
        }
    }

    /**
     * Take a school supply from the backpack.
     *
     * @param schoolSupply The school supply to take.
     * @return A new desktop with the school supply taken from the backpack.
     * @throws SchoolSupplyNotFoundException If the school supply is not in the backpack.
     */
    @Throws(SchoolSupplyNotFoundException::class)
    override fun takeSchoolSupplyFromBackpack(schoolSupply: SchoolSupply): Desktop {
        if (!schoolSuppliesInBackpack.contains(schoolSupply))
            throw SchoolSupplyNotFoundException(schoolSupply.rfidCode)
        else
            return DesktopImpl(
                schoolSupplies = schoolSupplies,
                schoolSupplyTypes = schoolSupplyTypes,
                schoolSuppliesInBackpack = schoolSuppliesInBackpack - schoolSupply
            )
    }
}
