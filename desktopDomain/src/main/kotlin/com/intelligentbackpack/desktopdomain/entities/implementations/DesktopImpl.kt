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
internal data class DesktopImpl(
    override val schoolSupplies: Set<SchoolSupply>,
    override val schoolSupplyTypes: Set<SchoolSupplyType>,
    override val schoolSuppliesInBackpack: Set<SchoolSupply> = emptySet(),
    override val backpack: Backpack? = null,
) : Desktop {

    /**
     * Add a school supply to the desktop.
     *
     * @param schoolSupply The school supply to add.
     * @throws TypeException If the type of the school supply is not in the desktop.
     * @throws DuplicateRFIDException If the RFID code of the school supply is already in the desktop.
     */
    @Throws(TypeException::class, DuplicateRFIDException::class)
    override fun addSchoolSupply(schoolSupply: SchoolSupply): Desktop =
        if (!schoolSupplyTypes.contains(schoolSupply.type)) {
            throw TypeException(schoolSupply.type)
        } else {
            if (schoolSupplies.map { it.rfidCode }.contains(schoolSupply.rfidCode)) {
                throw DuplicateRFIDException()
            } else {
                copy(schoolSupplies = schoolSupplies + schoolSupply)
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
    override fun putSchoolSupplyInBackpack(schoolSupply: SchoolSupply): Desktop {
        if (!isBackpackAssociated) {
            throw BackpackNotAssociatedException()
        } else {
            if (!schoolSupplies.contains(schoolSupply)) {
                throw SchoolSupplyNotFoundException(schoolSupply.rfidCode)
            } else {
                if (schoolSuppliesInBackpack.contains(schoolSupply)) {
                    throw AlreadyInBackpackException(schoolSupply)
                } else {
                    return copy(schoolSuppliesInBackpack = schoolSuppliesInBackpack + schoolSupply)
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
    override fun associateBackpack(backpack: Backpack): Desktop {
        if (isBackpackAssociated) {
            throw BackpackAlreadyAssociatedException()
        } else {
            return copy(backpack = backpack)
        }
    }

    /**
     * Take a school supply from the backpack.
     *
     * @param schoolSupply The school supply to take.
     * @throws SchoolSupplyNotFoundException If the school supply is not in the backpack.
     */
    @Throws(SchoolSupplyNotFoundException::class)
    override fun takeSchoolSupplyFromBackpack(schoolSupply: SchoolSupply): Desktop {
        if (!isBackpackAssociated) {
            throw BackpackNotAssociatedException()
        } else {
            if (!schoolSuppliesInBackpack.contains(schoolSupply)) {
                throw SchoolSupplyNotFoundException(schoolSupply.rfidCode)
            } else {
                return copy(schoolSuppliesInBackpack = schoolSuppliesInBackpack - schoolSupply)
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
    override fun disassociateBackpack(hash: String): Desktop {
        if (isBackpackAssociated && backpack!! == hash) {
            return copy(backpack = null, schoolSuppliesInBackpack = emptySet())
        } else {
            throw BackpackNotAssociatedException()
        }
    }
}
