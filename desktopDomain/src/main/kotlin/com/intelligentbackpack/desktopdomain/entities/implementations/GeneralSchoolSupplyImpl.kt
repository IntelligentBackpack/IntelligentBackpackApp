package com.intelligentbackpack.desktopdomain.entities.implementations

import com.intelligentbackpack.desktopdomain.entities.GeneralSchoolSupply
import com.intelligentbackpack.desktopdomain.entities.SchoolSupplyType
import com.intelligentbackpack.desktopdomain.entities.Subject

/**
 * Implementation of a general school supply.
 *
 * @property type The type of the school supply.
 * @property rfidCode The RFID code of the school supply.
 * @property description The description of the school supply.
 * @property subjects The subjects of the school supply.
 * @property replacedBy The school supplies that replace this one.
 * @property replace The school supplies that this one replaces.
 */
internal data class GeneralSchoolSupplyImpl(
    override val rfidCode: String
) : AbstractSchoolSupply<GeneralSchoolSupply>(), GeneralSchoolSupply {

    constructor(
        type: SchoolSupplyType,
        rfidCode: String,
        description: String,
        subjects: Set<Subject>,
        replacedBy: Set<GeneralSchoolSupply>,
        replace: Set<GeneralSchoolSupply>
    ) : this(rfidCode) {
        this.type = type
        this.description = description
        this.subjects = subjects
        this.replacedBy = replacedBy
        this.replace = replace
    }

    override lateinit var type: SchoolSupplyType
        private set
    override lateinit var description: String
        private set
    override lateinit var subjects: Set<Subject>
        private set
    override var replacedBy: Set<GeneralSchoolSupply> = setOf()
        private set
    override var replace: Set<GeneralSchoolSupply> = setOf()
        private set

    override fun copy(
        type: SchoolSupplyType,
        rfidCode: String,
        subjects: Set<Subject>,
        replacedBy: Set<GeneralSchoolSupply>,
        replace: Set<GeneralSchoolSupply>
    ): GeneralSchoolSupply = GeneralSchoolSupplyImpl(
        type = type,
        rfidCode = rfidCode,
        description = description,
        subjects = subjects,
        replacedBy = replacedBy,
        replace = replace
    )
}
