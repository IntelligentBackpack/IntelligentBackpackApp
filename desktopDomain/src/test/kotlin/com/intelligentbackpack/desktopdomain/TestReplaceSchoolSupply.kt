package com.intelligentbackpack.desktopdomain

import com.intelligentbackpack.desktopdomain.domainserivece.Replace.replace
import com.intelligentbackpack.desktopdomain.entities.GeneralSchoolSupply
import com.intelligentbackpack.desktopdomain.entities.SchoolSupplyTypes
import com.intelligentbackpack.desktopdomain.entities.Subjects
import com.intelligentbackpack.desktopdomain.exception.ReplaceException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TestReplaceSchoolSupply : StringSpec({

    val exerciseBook = SchoolSupplyTypes.create("Exercise book")
    val pencil = SchoolSupplyTypes.create("Pencil")
    val math = Subjects.create("Math")
    val physics = Subjects.create("Physics")
    val types = listOf(exerciseBook, pencil)
    val subjects = setOf(math, physics)
    val rfidCode1 = "FF:24:3E:C1"
    val rfidCode2 = "FF:24:3E:C2"

    "Replace a school supply of type exercise book with a school supply of type exercise book" {
        val exerciseBookOld = GeneralSchoolSupply
            .build(
                types = types,
                checkSubjects = subjects
            ) {
                this.type = exerciseBook
                this.rfidCode = rfidCode1
                this.subjects = setOf(math)
            }
        val exerciseBookNew = GeneralSchoolSupply
            .build(
                types = types,
                checkSubjects = subjects
            ) {
                this.type = exerciseBook
                this.rfidCode = rfidCode2
                this.subjects = setOf(math)
            }
        val replace = replace(setOf(exerciseBookNew), setOf(exerciseBookOld))
        replace.newSchoolSupplies.forEach {
            it.replace shouldBe setOf(exerciseBookOld)
        }
        replace.oldSchoolSupplies.forEach {
            it.replacedBy shouldBe setOf(exerciseBookNew)
        }
    }

    "Replace a school supply with another one with more subject" {
        val exerciseBookOld = GeneralSchoolSupply
            .build(
                types = types,
                checkSubjects = subjects
            ) {
                this.type = exerciseBook
                this.rfidCode = rfidCode1
                this.subjects = setOf(math)
            }
        val exerciseBookNew = GeneralSchoolSupply
            .build(
                types = types,
                checkSubjects = subjects
            ) {
                this.type = exerciseBook
                this.rfidCode = rfidCode2
                this.subjects = setOf(math, physics)
            }
        val replace = replace(setOf(exerciseBookNew), setOf(exerciseBookOld))
        replace.newSchoolSupplies.forEach {
            it.replace shouldBe setOf(exerciseBookOld)
        }
        replace.oldSchoolSupplies.forEach {
            it.replacedBy shouldBe setOf(exerciseBookNew)
        }
    }

    "Replace a school supply of type exercise book with a school supply of type pencil" {
        val exerciseBookOld = GeneralSchoolSupply
            .build(
                types = types,
                checkSubjects = subjects
            ) {
                this.type = exerciseBook
                this.rfidCode = rfidCode1
                this.subjects = setOf(math)
            }
        val pencilNew = GeneralSchoolSupply
            .build(
                types = types,
                checkSubjects = subjects
            ) {
                this.type = pencil
                this.rfidCode = rfidCode2
                this.subjects = setOf(math)
            }
        shouldThrow<ReplaceException> {
            replace(setOf(pencilNew), setOf(exerciseBookOld))
        }
    }

    "Replace a school supply of type exercise book with a school supply of type exercise book with different subjects" {
        val exerciseBookOld = GeneralSchoolSupply
            .build(
                types = types,
                checkSubjects = subjects
            ) {
                this.type = exerciseBook
                this.rfidCode = rfidCode1
                this.subjects = setOf(math)
            }
        val exerciseBookNew = GeneralSchoolSupply
            .build(
                types = types,
                checkSubjects = subjects
            ) {
                this.type = exerciseBook
                this.rfidCode = rfidCode2
                this.subjects = setOf(physics)
            }
        shouldThrow<ReplaceException> {
            replace(setOf(exerciseBookNew), setOf(exerciseBookOld))
        }
    }

    """Replace a school supply of type exercise book with a school supply
    of type exercise book with not all its subjects""" {
        val exerciseBookOld = GeneralSchoolSupply
            .build(
                types = types,
                checkSubjects = subjects
            ) {
                this.type = exerciseBook
                this.rfidCode = rfidCode1
                this.subjects = setOf(math, physics)
            }
        val exerciseBookNew = GeneralSchoolSupply
            .build(
                types = types,
                checkSubjects = subjects
            ) {
                this.type = exerciseBook
                this.rfidCode = rfidCode2
                this.subjects = setOf(math)
            }
        shouldThrow<ReplaceException> {
            replace(setOf(exerciseBookNew), setOf(exerciseBookOld))
        }
    }
})
