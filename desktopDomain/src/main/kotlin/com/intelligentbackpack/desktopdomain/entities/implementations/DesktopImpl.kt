package com.intelligentbackpack.desktopdomain.entities.implementations

import com.intelligentbackpack.desktopdomain.entities.Desktop
import com.intelligentbackpack.desktopdomain.entities.SchoolSupply
import com.intelligentbackpack.desktopdomain.entities.SchoolSupplyType
import com.intelligentbackpack.desktopdomain.exception.DuplicateRFIDException
import com.intelligentbackpack.desktopdomain.exception.TypeException

/**
 * Implementation of a desktop.
 *
 * @property schoolSupplies The school supplies of the desktop.
 * @property schoolSupplyTypes The types of the school supplies of the desktop.
 */
internal data class DesktopImpl(
    override val schoolSupplies: Set<SchoolSupply>,
    override val schoolSupplyTypes: Set<SchoolSupplyType>,
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
}
