package com.intelligentbackpack.desktopdomain.entities

import com.intelligentbackpack.desktopdomain.entities.implementations.GeneralSchoolSupplyImpl
import com.intelligentbackpack.desktopdomain.exception.TypeException
import com.intelligentbackpack.desktopdomain.policies.RFIDPolicy
import com.intelligentbackpack.desktopdomain.policies.ReplacePolicy

/**
 * Interface for a general school supply.
 */
interface GeneralSchoolSupply : MutableSchoolSupply<GeneralSchoolSupply> {
    /**
     * The description of the school supply.
     */
    val description: String

    companion object {

        /**
         * Builds a general school supply.
         *
         * @param types The types of the school supplies that can be built.
         * @param checkSubjects The subjects that are available.
         * @param block The builder block.
         * @return The general school supply built.
         * @throws IllegalArgumentException If the school supply is invalid
         * ( the [rfidCode] doesn't match with the [RFIDPolicy],
         * the [subjects] aren't in the [checkSubjects],
         * [replace] or [replacedBy] doesn't respect the [ReplacePolicy]).
         * @throws IllegalStateException If not all the properties are initialized.
         */
        inline fun build(
            types: List<SchoolSupplyType>,
            checkSubjects: Set<Subject>,
            block: Builder.() -> Unit
        ): GeneralSchoolSupply = Builder(types, checkSubjects).apply(block).build()
    }

    /**
     * Builder for a general school supply.
     *
     * @property types The types of the school supplies that can be built.
     * @property checkSubjects The subjects that are available.
     *
     */
    class Builder(
        types: List<SchoolSupplyType>,
        checkSubjects: Set<Subject>
    ) : SchoolSupplyBuilder<GeneralSchoolSupply>(types, checkSubjects) {
        /**
         * The description of the school supply default to "".
         */
        var description: String = ""

        /**
         * Builds the general school supply.
         *
         * @return The general school supply built.
         * @throws TypeException If the type of the school supply is not valid.
         */
        @Throws(TypeException::class)
        override fun specificBuilder(): GeneralSchoolSupply =
            if (type != SchoolSupplyTypes.BOOK)
                with(this) {
                    GeneralSchoolSupplyImpl(
                        type = type,
                        rfidCode = rfidCode,
                        description = description,
                        subjects = subjects,
                        replacedBy = replacedBy,
                        replace = replace,
                    )
                }
            else
                throw TypeException(type)
    }
}
