package com.intelligentbackpack.desktopdomain.entities

/**
 * Interface for a mutable school supply.
 */
interface MutableSchoolSupply<T : SchoolSupply> : SchoolSupply {

    /**
     * The school supply that replaces this one.
     */
    val replacedBy: Set<T>

    /**
     * The school supply that this one replaces.
     */
    val replace: Set<T>

    /**
     * Adds the given school supply to the school supplies that replace this one.
     * @param schoolSupply The school supply to add.
     * @return The school supply with the given school supply added.
     */
    fun addReplacedBy(schoolSupply: T): MutableSchoolSupply<T>

    /**
     * Adds the given school supplies to the school supplies that replace this one.
     * @param schoolSupplies The school supplies to add.
     * @return The school supply with the given school supplies added.
     */
    fun addReplacesBy(schoolSupplies: Set<T>): MutableSchoolSupply<T> =
        schoolSupplies.fold(this) { acc, schoolSupply -> acc.addReplacedBy(schoolSupply) }

    /**
     * Adds the given school supply to the school supplies that this one replaces.
     * @param schoolSupply The school supply to add.
     * @return The school supply with the given school supply added.
     */
    fun addReplace(schoolSupply: T): MutableSchoolSupply<T>

    /**
     * Adds the given school supplies to the school supplies that this one replaces.
     * @param schoolSupplies The school supplies to add.
     * @return The school supply with the given school supplies added.
     */
    fun addReplaces(schoolSupplies: Set<T>): MutableSchoolSupply<T> =
        schoolSupplies.fold(this) { acc, schoolSupply -> acc.addReplace(schoolSupply) }

    /**
     * Adds the given subjects to the school supply.
     * @param subjects The subjects to add.
     * @return The school supply with the given subjects added.
     */
    override fun addSubjects(subjects: Set<Subject>): MutableSchoolSupply<T>
}
