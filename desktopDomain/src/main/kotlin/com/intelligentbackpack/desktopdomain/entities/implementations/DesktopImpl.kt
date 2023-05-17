package com.intelligentbackpack.desktopdomain.entities.implementations

import com.intelligentbackpack.desktopdomain.entities.Backpack
import com.intelligentbackpack.desktopdomain.entities.Desktop
import com.intelligentbackpack.desktopdomain.entities.SchoolSupply
import com.intelligentbackpack.desktopdomain.entities.SchoolSupplyType
import com.intelligentbackpack.desktopdomain.exception.AlreadyInBackpackException
import com.intelligentbackpack.desktopdomain.exception.BackpackAlreadyAssociatedException
import com.intelligentbackpack.desktopdomain.exception.BackpackNotAssociatedException
import com.intelligentbackpack.desktopdomain.exception.DuplicateRFIDException
import com.intelligentbackpack.desktopdomain.exception.SchoolSupplyNotFoundException
import com.intelligentbackpack.desktopdomain.exception.TypeException
import kotlin.jvm.Throws

/**
 * Implementation of a desktop.
 *
 * @property schoolSupplies The school supplies of the desktop.
 * @property schoolSupplyTypes The types of the school supplies of the desktop.
 * @property schoolSuppliesInBackpack The school supplies in the backpack.
 * @property backpack The backpack of the user.
 */
internal class DesktopImpl(
    schoolSupplies: Set<SchoolSupply>,
    schoolSupplyTypes: Set<SchoolSupplyType>,
    schoolSuppliesInBackpack: Set<SchoolSupply> = emptySet(),
    backpack: Backpack? = null,
) : Desktop {

    override var schoolSupplies: Set<SchoolSupply> = schoolSupplies
        private set
    override var schoolSupplyTypes: Set<SchoolSupplyType> = schoolSupplyTypes
        private set
    override var schoolSuppliesInBackpack: Set<SchoolSupply> = schoolSuppliesInBackpack
        private set

    override var backpack: Backpack? = backpack
        private set

    /**
     * Add a school supply to the desktop.
     *
     * @param schoolSupply The school supply to add.
     * @throws TypeException If the type of the school supply is not in the desktop.
     * @throws DuplicateRFIDException If the RFID code of the school supply is already in the desktop.
     */
    @Throws(TypeException::class, DuplicateRFIDException::class)
    override fun addSchoolSupply(schoolSupply: SchoolSupply) =
        if (!schoolSupplyTypes.contains(schoolSupply.type)) {
            throw TypeException(schoolSupply.type)
        } else {
            if (schoolSupplies.map { it.rfidCode }.contains(schoolSupply.rfidCode)) {
                throw DuplicateRFIDException()
            } else {
                schoolSupplies = schoolSupplies + schoolSupply
            }
        }

    /**
     * Put a school supply in the backpack.
     *
     * @param schoolSupply The school supply to put in the backpack.
     * @throws SchoolSupplyNotFoundException If the school supply is not in the desktop.
     * @throws AlreadyInBackpackException If the school supply is already in the backpack.
     */
    @Throws(SchoolSupplyNotFoundException::class)
    override fun putSchoolSupplyInBackpack(schoolSupply: SchoolSupply) {
        if (!isBackpackAssociated) {
            throw BackpackNotAssociatedException()
        } else {
            if (!schoolSupplies.contains(schoolSupply)) {
                throw SchoolSupplyNotFoundException(schoolSupply.rfidCode)
            } else {
                if (schoolSuppliesInBackpack.contains(schoolSupply)) {
                    throw AlreadyInBackpackException(schoolSupply)
                } else {
                    schoolSuppliesInBackpack = schoolSuppliesInBackpack + schoolSupply
                }
            }
        }
    }

    /**
     * Connects the backpack for the desktop's user.
     *
     * @throws BackpackAlreadyAssociatedException If a backpack is already connected.
     */
    @Throws(BackpackAlreadyAssociatedException::class)
    override fun associateBackpack(backpack: Backpack) {
        if (isBackpackAssociated) {
            throw BackpackAlreadyAssociatedException()
        } else {
            this.backpack = backpack
        }
    }

    /**
     * Take a school supply from the backpack.
     *
     * @param schoolSupply The school supply to take.
     * @throws SchoolSupplyNotFoundException If the school supply is not in the backpack.
     */
    @Throws(SchoolSupplyNotFoundException::class)
    override fun takeSchoolSupplyFromBackpack(schoolSupply: SchoolSupply) {
        if (!isBackpackAssociated) {
            throw BackpackNotAssociatedException()
        } else {
            if (!schoolSuppliesInBackpack.contains(schoolSupply)) {
                throw SchoolSupplyNotFoundException(schoolSupply.rfidCode)
            } else {
                schoolSuppliesInBackpack = schoolSuppliesInBackpack - schoolSupply
            }
        }
    }

    /**
     * Disconnects the backpack for the desktop's user.
     *
     * @param hash The hash of the backpack to disconnect.
     * @throws BackpackNotAssociatedException If no backpack is connected.
     */
    @Throws(BackpackNotAssociatedException::class)
    override fun disassociateBackpack(hash: String) {
        if (isBackpackAssociated && backpack!! == hash) {
            backpack = null
            schoolSuppliesInBackpack = emptySet()
        } else {
            throw BackpackNotAssociatedException()
        }
    }
}
