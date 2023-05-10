package com.intelligentbackpack.desktopdomain.entities

import com.intelligentbackpack.desktopdomain.exception.RFIDFormatException
import com.intelligentbackpack.desktopdomain.exception.ReplaceException
import com.intelligentbackpack.desktopdomain.exception.SubjectException
import com.intelligentbackpack.desktopdomain.exception.TypeException
import com.intelligentbackpack.desktopdomain.policies.RFIDPolicy
import com.intelligentbackpack.desktopdomain.policies.ReplacePolicy

/**
 * Builder for a school supply.
 *
 * @param T The type of the school supply.
 * @property types The types of the school supplies that can be built.
 * @property checkSubjects The subjects that are available.
 *
 */
abstract class SchoolSupplyBuilder<T : SchoolSupply>(
    private val types: List<SchoolSupplyType>,
    private val checkSubjects: Set<Subject>,
) {
    /**
     * The type of the school supply.
     */
    lateinit var type: SchoolSupplyType

    /**
     * The RFID code of the school supply.
     */
    lateinit var rfidCode: String

    /**
     * The subjects of the school supply.
     */
    lateinit var subjects: Set<Subject>

    /**
     * The school supplies that replace this one.
     */
    var replacedBy: Set<T> = setOf()

    /**
     * The school supplies that this one replaces.
     */
    var replace: Set<T> = setOf()

    /**
     * Builds the specific school supply.
     */
    abstract fun specificBuilder(): T

    /**
     * Builds the school supply.
     *
     * @return The school supply built.
     * @throws IllegalStateException If not all properties are initialized.
     * @throws TypeException If the type of the school supply is not valid.
     * @throws RFIDFormatException If the RFID code of the school supply is not valid.
     * @throws SubjectException If the subjects of the school supply are not valid.
     * @throws ReplaceException If the school supplies that replace this one or the school supplies that this one replaces are not valid.
     * @throws IllegalArgumentException for any other reason.
     */
    @Throws(
        IllegalStateException::class,
        TypeException::class,
        RFIDFormatException::class,
        SubjectException::class,
        ReplaceException::class,
        IllegalArgumentException::class
    )
    open fun build(): T =
        if (this::type.isInitialized &&
            this::rfidCode.isInitialized &&
            this::subjects.isInitialized
        )
            if (types.contains(type))
                if (RFIDPolicy.isValid(rfidCode))
                    if (subjects.all { checkSubjects.contains(it) } ||
                        (subjects.contains(Subjects.ALL) && subjects.size == 1)
                    ) {
                        val built = specificBuilder()
                        if (ReplacePolicy.isValid(setOf(built), replace) || replace.isEmpty())
                            if (ReplacePolicy.isValid(replacedBy, setOf(built)) || replacedBy.isEmpty())
                                built
                            else
                                throw ReplaceException()
                        else
                            throw ReplaceException()
                    } else
                        throw SubjectException(subjects)
                else
                    throw RFIDFormatException()
            else
                throw TypeException(type)
        else
            throw IllegalStateException("Not all properties are initialized")
}
