package com.intelligentbackpack.desktopdomain

import com.intelligentbackpack.desktopdomain.entities.SchoolSupplyTypes
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TestSchoolSupplyType : StringSpec({
    "Create a school supply type with name Pencil" {
        val name = "Pencil"
        val pencil = SchoolSupplyTypes.create(name)
        pencil shouldBe name
    }

    "Create a school supply with lower case name" {
        val name = "pencil"
        val pencil = SchoolSupplyTypes.create(name)
        pencil shouldBe "Pencil"
    }

    "A book as school supply type should have name Book" {
        val name = "Book"
        val book = SchoolSupplyTypes.BOOK
        book shouldBe name
    }
})
