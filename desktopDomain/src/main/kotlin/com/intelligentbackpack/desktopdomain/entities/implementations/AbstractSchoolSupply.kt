package com.intelligentbackpack.desktopdomain.entities.implementations

import com.intelligentbackpack.desktopdomain.entities.MutableSchoolSupply
import com.intelligentbackpack.desktopdomain.entities.SchoolSupply
import com.intelligentbackpack.desktopdomain.entities.SchoolSupplyType
import com.intelligentbackpack.desktopdomain.entities.Subject
import com.intelligentbackpack.desktopdomain.exception.ReplaceException
import com.intelligentbackpack.desktopdomain.policies.ReplacePolicy
import kotlin.jvm.Throws

/**
 * Abstract implementation of a school supply.
 *
 * @property type The type of the school supply.
 * @property rfidCode The RFID code of the school supply.
 * @property subjects The subjects of the school supply.
 * @property replacedBy The school supplies that replace this one.
 * @property replace The school supplies that this one replaces.
 */
abstract class AbstractSchoolSupply<T : SchoolSupply> : MutableSchoolSupply<T> {

    /**
     * The type of the school supply.
     * @param type The type of the school supply.
     * @param rfidCode The RFID code of the school supply.
     * @param subjects The subjects of the school supply.
     * @param replacedBy The school supplies that replace this one.
     * @param replace The school supplies that this one replaces.
     * @return The school supply with the given type.
     */
    protected abstract fun copy(
        type: SchoolSupplyType = this.type,
        rfidCode: String = this.rfidCode,
        subjects: Set<Subject> = this.subjects,
        replacedBy: Set<T> = this.replacedBy,
        replace: Set<T> = this.replace,
    ): MutableSchoolSupply<T>

    /**
     * Add the given school supply to the list of school supplies that replace this one.
     * @param schoolSupply The school supply to add.
     * @return The school supply with the given school supply added to the list of school supplies that replace this one.
     * @throws ReplaceException If the school supply that replace doesn't respect the [ReplacePolicy].
     */
    @Throws(ReplaceException::class)
    override fun addReplacedBy(schoolSupply: T): MutableSchoolSupply<T> {
        if (ReplacePolicy.isValid(setOf(schoolSupply), setOf(this)))
            return this.copy(replacedBy = replacedBy + schoolSupply)
        else
            throw ReplaceException()
    }

    /**
     * Add the given school supply to the list of school supplies that this one replaces.
     * @param schoolSupply The school supply to add.
     * @return The school supply with the given school supply added to the list of school supplies that this one replaces.
     * @throws ReplaceException If the school supply to replace doesn't respect the [ReplacePolicy].
     */
    @Throws(ReplaceException::class)
    override fun addReplace(schoolSupply: T): MutableSchoolSupply<T> {
        if (ReplacePolicy.isValid(setOf(this), setOf(schoolSupply)))
            return this.copy(replace = replace + schoolSupply)
        else
            throw ReplaceException()
    }

    override fun addSubjects(subjects: Set<Subject>) =
        this.copy(subjects = this.subjects + subjects)
}
