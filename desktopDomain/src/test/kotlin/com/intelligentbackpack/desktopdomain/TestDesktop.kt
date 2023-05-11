package com.intelligentbackpack.desktopdomain

import com.intelligentbackpack.desktopdomain.entities.BookCopy
import com.intelligentbackpack.desktopdomain.entities.Desktop
import com.intelligentbackpack.desktopdomain.entities.SchoolSupplyTypes
import com.intelligentbackpack.desktopdomain.exception.DuplicateRFIDException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TestDesktop : StringSpec({

    val bookCopy = BookCopy.build() {
        isbn = "978885152159X"
        rfidCode = "FF:24:3E:C1"
        title = "The Lord of the Rings"
        authors = listOf("J. R. R. Tolkien")
    }

    "Create a Desktop" {
        val desktop = Desktop.create(setOf(bookCopy))
        desktop.schoolSupplies.size shouldBe 1
        desktop.schoolSupplyTypes.size shouldBe 1
        desktop.schoolSupplyTypes shouldBe setOf(SchoolSupplyTypes.BOOK)
    }

    "Add a School Supply to Desktop" {
        val desktop = Desktop.create(setOf(bookCopy))
        val bookCopy2 = BookCopy.build() {
            isbn = "978885152159X"
            rfidCode = "FF:24:3E:C2"
            title = "The Lord of the Rings"
            authors = listOf("J. R. R. Tolkien")
        }
        val newDesktop = desktop.addSchoolSupply(bookCopy2)
        desktop.schoolSupplies.size shouldBe 1
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
})
