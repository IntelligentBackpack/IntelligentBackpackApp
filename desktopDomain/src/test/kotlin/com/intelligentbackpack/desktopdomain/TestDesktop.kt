package com.intelligentbackpack.desktopdomain

import com.intelligentbackpack.desktopdomain.entities.Backpack
import com.intelligentbackpack.desktopdomain.entities.BookCopy
import com.intelligentbackpack.desktopdomain.entities.Desktop
import com.intelligentbackpack.desktopdomain.entities.GeneralSchoolSupply
import com.intelligentbackpack.desktopdomain.entities.SchoolSupplyTypes
import com.intelligentbackpack.desktopdomain.entities.Subjects
import com.intelligentbackpack.desktopdomain.exception.AlreadyPresentType
import com.intelligentbackpack.desktopdomain.exception.DuplicateBackpack
import com.intelligentbackpack.desktopdomain.exception.DuplicateRFIDException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TestDesktop : StringSpec({

    val english = Subjects.create("English")
    val subjects = setOf(english)
    val pencilType = SchoolSupplyTypes.create("Pencil")
    val bookCopy = BookCopy.build(subjects) {
        isbn = "978885152159X"
        rfidCode = "FF:24:3E:C1"
        this.subjects = subjects
        title = "The Lord of the Rings"
        authors = listOf("J. R. R. Tolkien")
    }
    val pencil = GeneralSchoolSupply
        .build(
            listOf(pencilType),
            subjects
        ) {
            rfidCode = "FF:24:3E:C2"
            this.subjects = subjects
            type = pencilType
            description = "A pencil"
        }

    "Create a Desktop" {
        val backpack = Backpack.create("Backpack1")
        val desktop = Desktop.create(setOf(backpack), setOf(bookCopy, pencil), setOf(pencilType), subjects)
        desktop.backpacks.size shouldBe 1
        desktop.backpacks.first() shouldBe backpack
        desktop.schoolSupplies.size shouldBe 2
        desktop.schoolSupplyTypes.size shouldBe 2
        desktop.schoolSupplyTypes shouldBe setOf(SchoolSupplyTypes.BOOK, pencilType)
    }

    "Add a subject to Desktop" {
        val desktop = Desktop.create(setOf(), setOf(bookCopy, pencil), setOf(pencilType), subjects)
        val newSubject = Subjects.create("Math")
        val newDesktop = desktop.addSubject(newSubject)
        newDesktop.subjects.size shouldBe 3
        newDesktop.subjects shouldBe (subjects + newSubject + Subjects.ALL).toSet()
    }

    "Add an already present subject to Desktop" {
        val desktop = Desktop.create(setOf(), setOf(bookCopy, pencil), setOf(pencilType), subjects)
        val newDesktop = desktop.addSubject(english)
        newDesktop.subjects.size shouldBe 2
        newDesktop.subjects shouldBe (subjects + Subjects.ALL).toSet()
    }

    "Add a School Supply type to Desktop" {
        val desktop = Desktop.create(setOf(), setOf(bookCopy, pencil), setOf(pencilType), subjects)
        val exerciseBookType = SchoolSupplyTypes.create("Exercise book")
        val newDesktop = desktop.addSchoolSupplyType(exerciseBookType)
        newDesktop.schoolSupplyTypes.size shouldBe 3
        newDesktop.schoolSupplyTypes shouldBe setOf(pencilType, exerciseBookType, SchoolSupplyTypes.BOOK)
    }

    "Add an already present type to Desktop" {
        val desktop = Desktop.create(setOf(), setOf(bookCopy, pencil), setOf(pencilType), subjects)
        shouldThrow<AlreadyPresentType> {
            desktop.addSchoolSupplyType(pencilType)
        }
    }

    "Add a Backpack to Desktop" {
        val desktop = Desktop.create(setOf(), setOf(bookCopy, pencil), setOf(pencilType), subjects)
        val backpack = Backpack.create("Backpack1")
        val newDesktop = desktop.addBackpack(backpack)
        newDesktop.backpacks.size shouldBe 1
        newDesktop.backpacks.first() shouldBe backpack
    }

    "Add an already present Backpack to Desktop" {
        val backpack = Backpack.create("Backpack1")
        val desktop = Desktop.create(setOf(backpack), setOf(bookCopy, pencil), setOf(pencilType), subjects)
        shouldThrow<DuplicateBackpack> {
            desktop.addBackpack(backpack)
        }
    }

    "Add a School Supply to Desktop" {
        val backpack = Backpack.create("Backpack1")
        val desktop = Desktop.create(setOf(backpack), setOf(bookCopy), setOf(pencilType), subjects)
        val newDesktop = desktop.addSchoolSupply(pencil)
        newDesktop.schoolSupplies.size shouldBe 2
        newDesktop.schoolSupplies shouldBe setOf(bookCopy, pencil)
    }

    "Add an already present School Supply to Desktop" {
        val backpack = Backpack.create("Backpack1")
        val desktop = Desktop.create(setOf(backpack), setOf(bookCopy, pencil), setOf(pencilType), subjects)
        shouldThrow<DuplicateRFIDException> {
            desktop.addSchoolSupply(pencil)
        }
    }

    "Replace a School Supply in Desktop" {
        val backpack = Backpack.create("Backpack1")
        val desktop = Desktop.create(setOf(backpack), setOf(bookCopy, pencil), setOf(pencilType), subjects)
        val newBookCopy = BookCopy.build(subjects) {
            isbn = "978885152159X"
            rfidCode = "FF:24:3E:C3"
            this.subjects = subjects
            title = "The Lord of the Rings"
            authors = listOf("J. R. R. Tolkien")
        }
        val newDesktop = desktop.replaceSchoolSupplies(
            setOf(newBookCopy), setOf(bookCopy)
        )
        newDesktop.schoolSupplies.size shouldBe 3
        newDesktop.schoolSupplies shouldBe setOf(bookCopy, newBookCopy, pencil)
        (newDesktop.schoolSupplies.first { it == bookCopy } as BookCopy).replacedBy shouldBe setOf(newBookCopy)
        (newDesktop.schoolSupplies.first { it == bookCopy } as BookCopy).replace shouldBe setOf()
        (newDesktop.schoolSupplies.first { it == newBookCopy } as BookCopy).replace shouldBe setOf(bookCopy)
        (newDesktop.schoolSupplies.first { it == newBookCopy } as BookCopy).replacedBy shouldBe setOf()
    }
})
