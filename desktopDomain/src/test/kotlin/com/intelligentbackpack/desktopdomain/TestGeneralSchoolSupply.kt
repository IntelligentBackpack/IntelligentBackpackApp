package com.intelligentbackpack.desktopdomain

import com.intelligentbackpack.desktopdomain.entities.GeneralSchoolSupply
import com.intelligentbackpack.desktopdomain.entities.SchoolSupplyTypes
import com.intelligentbackpack.desktopdomain.entities.Subjects
import com.intelligentbackpack.desktopdomain.exception.RFIDFormatException
import com.intelligentbackpack.desktopdomain.exception.SubjectException
import com.intelligentbackpack.desktopdomain.exception.TypeException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TestGeneralSchoolSupply : StringSpec({

    val pencilType = SchoolSupplyTypes.create("Pencil")
    val exerciseBook = SchoolSupplyTypes.create("Exercise book")
    val math = Subjects.create("Math")
    val physics = Subjects.create("Physics")
    val rfidCode = "FF:24:3E:C1"
    val description = "A pencil"

    "Create a school supply of type pencil for all subjects" {
        val schoolSupply = GeneralSchoolSupply
            .build(
                types = listOf(pencilType),
                checkSubjects = setOf()
            ) {
                this.type = pencilType
                this.rfidCode = rfidCode
                this.subjects = setOf(Subjects.ALL)
                this.description = description
            }
        schoolSupply.type shouldBe pencilType
    }

    "Create a school supply from given subjects" {
        val schoolSupply = GeneralSchoolSupply
            .build(
                types = listOf(exerciseBook),
                checkSubjects = setOf(math, physics)
            ) {
                this.type = exerciseBook
                this.rfidCode = rfidCode
                this.subjects = setOf(math)
                this.description = description
            }
        schoolSupply.subjects shouldBe setOf(math)
    }

    "Can't create a school supply of type Book use book copy instead" {
        val book = SchoolSupplyTypes.BOOK
        shouldThrow<TypeException> {
            GeneralSchoolSupply
                .build(
                    types = listOf(book),
                    checkSubjects = setOf()
                ) {
                    this.type = book
                    this.rfidCode = rfidCode
                    this.subjects = setOf(Subjects.ALL)
                }
        }
    }

    "Create a school supply with invalid RFID code non pair" {
        shouldThrow<RFIDFormatException> {
            GeneralSchoolSupply
                .build(
                    types = listOf(pencilType),
                    checkSubjects = setOf()
                ) {
                    this.type = pencilType
                    this.rfidCode = "FF:24:3E:C"
                    this.subjects = setOf(Subjects.ALL)
                }
        }
    }

    "Create a school supply with invalid RFID code non hexadecimal" {
        val pencil = SchoolSupplyTypes.create("Pencil")
        shouldThrow<RFIDFormatException> {
            GeneralSchoolSupply
                .build(
                    types = listOf(pencil),
                    checkSubjects = setOf()
                ) {
                    this.type = pencil
                    this.rfidCode = "FF:24:3E:G1"
                    this.subjects = setOf(Subjects.ALL)
                }
        }
    }

    "Create a school supply with invalid RFID code non colon separated" {
        shouldThrow<RFIDFormatException> {
            GeneralSchoolSupply
                .build(
                    types = listOf(pencilType),
                    checkSubjects = setOf()
                ) {
                    this.type = pencilType
                    this.rfidCode = "FF:243EG1"
                    this.subjects = setOf(Subjects.ALL)
                }
        }
    }

    "Create a school supply with invalid subjects" {
        val chemistry = Subjects.create("Chemistry")
        shouldThrow<SubjectException> {
            GeneralSchoolSupply
                .build(
                    types = listOf(pencilType),
                    checkSubjects = setOf(math, physics)
                ) {
                    this.type = pencilType
                    this.rfidCode = rfidCode
                    this.subjects = setOf(math, chemistry)
                }
        }
    }

    "Create a school supply with a type not in the given" {
        shouldThrow<TypeException> {
            GeneralSchoolSupply
                .build(
                    types = listOf(pencilType),
                    checkSubjects = setOf()
                ) {
                    this.type = exerciseBook
                    this.rfidCode = rfidCode
                    this.subjects = setOf(Subjects.ALL)
                    this.description = description
                }
        }
    }

    "Create a school supply without description" {
        val pencil = GeneralSchoolSupply
            .build(
                types = listOf(pencilType),
                checkSubjects = setOf()
            ) {
                this.type = pencilType
                this.rfidCode = rfidCode
                this.subjects = setOf(Subjects.ALL)
            }
        pencil.description shouldBe ""
    }
})
