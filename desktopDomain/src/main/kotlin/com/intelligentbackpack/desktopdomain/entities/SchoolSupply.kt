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

    /**
     * The subjects of the school supply.
     */
    val subjects: Set<Subject>

    /**
     * Adds the given subjects to the school supply.
     * @param subjects The subjects to add.
     * @return The school supply with the given subjects added.
     */
    fun addSubjects(subjects: Set<Subject>): SchoolSupply
}
