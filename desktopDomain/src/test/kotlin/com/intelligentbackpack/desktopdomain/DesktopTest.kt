package com.intelligentbackpack.desktopdomain

import com.intelligentbackpack.desktopdomain.entities.Book
import com.intelligentbackpack.desktopdomain.entities.BookCopy
import com.intelligentbackpack.desktopdomain.entities.Desktop
import com.intelligentbackpack.desktopdomain.entities.SchoolSupplyTypes
import com.intelligentbackpack.desktopdomain.exception.AlreadyInBackpackException
import com.intelligentbackpack.desktopdomain.exception.BackpackAlreadyAssociatedException
import com.intelligentbackpack.desktopdomain.exception.BackpackNotAssociatedException
import com.intelligentbackpack.desktopdomain.exception.DuplicateRFIDException
import com.intelligentbackpack.desktopdomain.exception.SchoolSupplyNotFoundException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class DesktopTest : StringSpec({

    val title = "The Lord of the Rings"
    val authors = setOf("J. R. R. Tolkien")
    val rfidCode = "FF:24:3E:C1"
    val isbn = "9788843025343"
    val book = Book.build {
        this.isbn = isbn
        this.title = title
        this.authors = authors
    }
    val bookCopy = BookCopy.build {
        this.rfidCode = rfidCode
        this.book = book
    }
    val backpack = "Backpack"

    "Create a Desktop" {
        val desktop = Desktop.create(setOf(bookCopy))
        desktop.schoolSupplies.size shouldBe 1
        desktop.schoolSupplyTypes.size shouldBe 1
        desktop.schoolSupplyTypes shouldBe setOf(SchoolSupplyTypes.BOOK)
    }

    "Add a School Supply to Desktop" {
        val desktop = Desktop.create(setOf(bookCopy))
        val bookCopy2 = BookCopy.build {
            this.rfidCode = "FF:24:3E:C2"
            this.book = book
        }
        val newDesktop = desktop.addSchoolSupply(bookCopy2)
        newDesktop.schoolSupplies.size shouldBe 2
        newDesktop.schoolSupplyTypes.size shouldBe 1
        newDesktop.schoolSupplyTypes shouldBe setOf(SchoolSupplyTypes.BOOK)
    }

    "Add an already present School Supply to Desktop" {
        val desktop = Desktop.create(setOf(bookCopy))
        shouldThrow<DuplicateRFIDException> {
            desktop.addSchoolSupply(bookCopy)
        }
    }

    "Put a School Supply in Backpack" {
        val desktop = Desktop.create(setOf(bookCopy), backpack = backpack)
        val newDesktop = desktop.putSchoolSupplyInBackpack(bookCopy)
        newDesktop.schoolSuppliesInBackpack.size shouldBe 1
        newDesktop.schoolSupplies shouldBe setOf(bookCopy)
    }

    "Put a School Supply in Backpack but Backpack is not associated" {
        val desktop = Desktop.create(setOf(bookCopy))
        shouldThrow<BackpackNotAssociatedException> {
            desktop.putSchoolSupplyInBackpack(bookCopy)
        }
    }

    "Put a School Supply not in Desktop in Backpack" {
        val desktop = Desktop.create(setOf(bookCopy), backpack = backpack)
        val bookCopy2 = BookCopy.build {
            this.rfidCode = "FF:24:3E:C2"
            this.book = book
        }
        shouldThrow<SchoolSupplyNotFoundException> {
            desktop.putSchoolSupplyInBackpack(bookCopy2)
        }
    }

    "Put a School Supply already in Backpack in Backpack" {
        val desktop = Desktop.create(setOf(bookCopy), setOf(bookCopy), backpack = backpack)
        shouldThrow<AlreadyInBackpackException> {
            desktop.putSchoolSupplyInBackpack(bookCopy)
        }
    }

    "Take a School Supply from Backpack" {
        val desktop = Desktop.create(setOf(bookCopy), setOf(bookCopy), backpack = backpack)
        val newDesktop = desktop.takeSchoolSupplyFromBackpack(bookCopy)
        newDesktop.schoolSuppliesInBackpack.size shouldBe 0
        newDesktop.schoolSupplies shouldBe setOf(bookCopy)
    }

    "Take a School Supply not in Backpack from Backpack" {
        val desktop = Desktop.create(setOf(bookCopy), backpack = backpack)
        shouldThrow<SchoolSupplyNotFoundException> {
            desktop.takeSchoolSupplyFromBackpack(bookCopy)
        }
    }

    "Take a School Supply from Backpack but it is not associated" {
        val desktop = Desktop.create(setOf(bookCopy))
        shouldThrow<BackpackNotAssociatedException> {
            desktop.takeSchoolSupplyFromBackpack(bookCopy)
        }
    }

    "Associate backpack" {
        val desktop = Desktop.create(setOf(bookCopy))
        desktop.isBackpackAssociated shouldBe false
        val newDesktop = desktop.associateBackpack(backpack)
        newDesktop.isBackpackAssociated shouldBe true
    }

    "Associate a backpack to a desktop already with a backpack associated" {
        val desktop = Desktop.create(setOf(bookCopy), backpack = backpack)
        shouldThrow<BackpackAlreadyAssociatedException> {
            desktop.associateBackpack("backpack2")
        }
    }

    "Disassociate backpack" {
        val desktop = Desktop.create(setOf(bookCopy), setOf(bookCopy), backpack = backpack)
        desktop.isBackpackAssociated shouldBe true
        val newDesktop = desktop.disassociateBackpack(backpack)
        newDesktop.isBackpackAssociated shouldBe false
        newDesktop.schoolSuppliesInBackpack shouldBe emptySet()
    }

    "Disassociate a backpack from a desktop without a backpack associated" {
        val desktop = Desktop.create(setOf(bookCopy))
        shouldThrow<BackpackNotAssociatedException> {
            desktop.disassociateBackpack(backpack)
        }
    }

    "Disassociate a backpack from a desktop with a different backpack associated" {
        val desktop = Desktop.create(setOf(bookCopy), backpack = "backpack2")
        shouldThrow<BackpackNotAssociatedException> {
            desktop.disassociateBackpack(backpack)
        }
    }
})
