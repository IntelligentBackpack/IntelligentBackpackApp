package com.intelligentbackpack.desktopdomain.policies

import com.intelligentbackpack.desktopdomain.entities.SchoolSupply

/**
 * A policy that determines whether new school supplies are valid replacements for old ones.
 */
object ReplacePolicy {

    /**
     * Checks whether new school supplies are valid replacements for old ones.
     * New school supplies are valid replacements for old ones if:
     * - they are of the same type;
     * - they have different RFID codes;
     * - they use the same subjects or more.
     *
     * @param newSchoolSupplies the new school supplies.
     * @param oldSchoolSupplies the old school supplies.
     * @return true if the new school supplies are valid replacements for the old ones, false otherwise.
     */
    fun isValid(newSchoolSupplies: Set<SchoolSupply>, oldSchoolSupplies: Set<SchoolSupply>): Boolean =
        checkSameType(newSchoolSupplies, oldSchoolSupplies) &&
            checkDifferentCode(newSchoolSupplies, oldSchoolSupplies) &&
            checkUseTheSameSubjectsOrMore(newSchoolSupplies, oldSchoolSupplies)

    private fun checkDifferentCode(newSchoolSupplies: Set<SchoolSupply>, oldSupplies: Set<SchoolSupply>): Boolean =
        (newSchoolSupplies.map { it.rfidCode }.toSet() + oldSupplies.map { it.rfidCode }.toSet())
            .size == newSchoolSupplies.size + oldSupplies.size

    private fun checkSameType(newSchoolSupplies: Set<SchoolSupply>, oldSupplies: Set<SchoolSupply>): Boolean =
        newSchoolSupplies.map { it.type }.toSet() == oldSupplies.map { it.type }.toSet()

    private fun checkUseTheSameSubjectsOrMore(
        newSchoolSupplies: Set<SchoolSupply>,
        oldSupplies: Set<SchoolSupply>
    ): Boolean =
        newSchoolSupplies.flatMap { it.subjects }.toSet().containsAll(oldSupplies.flatMap { it.subjects }.toSet())
}
