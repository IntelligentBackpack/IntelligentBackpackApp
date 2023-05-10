package com.intelligentbackpack.desktopdomain.domainserivece

import com.intelligentbackpack.desktopdomain.entities.MutableSchoolSupply
import com.intelligentbackpack.desktopdomain.entities.SchoolSupply
import com.intelligentbackpack.desktopdomain.exception.ReplaceException
import com.intelligentbackpack.desktopdomain.policies.ReplacePolicy

object Replace {

    /**
     * A domain service that replaces old school supplies with new ones.
     *
     * @param T the type of school supplies that can be replaced.
     * @property newSchoolSupplies the new school supplies.
     * @property oldSchoolSupplies the old school supplies.
     */
    class Replace<T : SchoolSupply>(
        val newSchoolSupplies: Set<MutableSchoolSupply<T>>,
        val oldSchoolSupplies: Set<MutableSchoolSupply<T>>
    )

    /**
     * Creates a new [Replace] instance.
     *
     * @param T the type of school supplies that can be replaced.
     * @param newSchoolSupplies the new school supplies.
     * @param oldSchoolSupplies the old school supplies.
     * @return a new [Replace] instance.
     * @throws IllegalArgumentException if the new school supplies are not valid replacements for the old ones.
     */
    fun <T : MutableSchoolSupply<T>> replace(newSchoolSupplies: Set<T>, oldSchoolSupplies: Set<T>): Replace<T> {
        if (!ReplacePolicy.isValid(newSchoolSupplies, oldSchoolSupplies)) {
            throw ReplaceException()
        }
        return Replace(
            newSchoolSupplies.map { it.addReplaces(oldSchoolSupplies) }.toSet(),
            oldSchoolSupplies.map { it.addReplacesBy(newSchoolSupplies) }.toSet()
        )
    }
}
