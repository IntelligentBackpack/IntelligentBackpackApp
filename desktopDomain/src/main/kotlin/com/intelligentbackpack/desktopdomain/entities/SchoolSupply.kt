package com.intelligentbackpack.desktopdomain.entities

/**
 * Interface for a school supply.
 */
interface SchoolSupply {
    /**
     * The type of the school supply.
     */
    val type: SchoolSupplyType

    /**
     * The RFID code of the school supply.
     */
    val rfidCode: String
}
