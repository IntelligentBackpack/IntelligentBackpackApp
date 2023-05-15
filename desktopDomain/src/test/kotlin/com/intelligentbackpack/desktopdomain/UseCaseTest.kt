package com.intelligentbackpack.desktopdomain

import com.intelligentbackpack.desktopdomain.entities.Book
import com.intelligentbackpack.desktopdomain.entities.BookCopy
import com.intelligentbackpack.desktopdomain.entities.Desktop
import com.intelligentbackpack.desktopdomain.entities.SchoolSupply
import com.intelligentbackpack.desktopdomain.repository.DesktopDomainRepository
import com.intelligentbackpack.desktopdomain.usecase.DesktopUseCase
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking

class UseCaseTest : StringSpec({

    val title = "The Lord of the Rings"
    val authors = setOf("J. R. R. Tolkien")
    val rfidCode1 = "FF:24:3E:C1"
    val rfidCode2 = "FF:24:3E:C2"
    val isbn = "9788843025343"
    val book = Book.build {
        this.isbn = isbn
        this.title = title
        this.authors = authors
    }
    val bookCopy1 = BookCopy.build {
        this.rfidCode = rfidCode1
        this.book = book
    }
    val bookCopy2 = BookCopy.build {
        this.rfidCode = rfidCode2
        this.book = book
    }

    val repository = mockk<DesktopDomainRepository>(relaxed = true)

    "Get Desktop" {
        val desktop = Desktop.create(
            schoolSupplies = setOf(bookCopy1),
            schoolSuppliesInBackpack = setOf()
        )
        val useCase = DesktopUseCase(repository)
        val desktopSlot = slot<(Desktop) -> Unit>()
        coEvery {
            repository.getDesktop(capture(desktopSlot), any())
        } answers {
            desktopSlot.captured(desktop)
        }
        useCase.getDesktop({
            it shouldBe desktop
        }, {
            assert(false)
        })
    }

    "Add a School Supply to Desktop" {
        val desktop = Desktop.create(
            schoolSupplies = setOf(bookCopy1),
            schoolSuppliesInBackpack = setOf()
        )
        runBlocking {
            val useCase = DesktopUseCase(repository)
            val schoolSupply = slot<(Set<SchoolSupply>) -> Unit>()
            val desktopSlot = slot<(Desktop) -> Unit>()
            coEvery {
                repository.getDesktop(success = capture(desktopSlot), error = any())
            } answers {
                firstArg<(Desktop) -> Unit>().invoke(desktop)
            }
            coEvery {
                repository.addSchoolSupply(
                    schoolSupply = any(),
                    success = capture(schoolSupply),
                    error = any()
                )
            } answers {
                schoolSupply.captured(desktop.schoolSupplies + bookCopy2)
            }
            useCase.addSchoolSupply(
                schoolSupply = bookCopy2,
                success = {
                    it.schoolSupplies shouldBe setOf(bookCopy1, bookCopy2)
                },
                error = { assert(false) }
            )
        }
    }

    "Get School Supplies in Desktop" {
        val desktop = Desktop.create(
            schoolSupplies = setOf(bookCopy1),
            schoolSuppliesInBackpack = setOf()
        )
        val useCase = DesktopUseCase(repository)
        coEvery {
            repository.getDesktop(any(), error = any())
        } answers {
            firstArg<(Desktop) -> Unit>().invoke(desktop)
        }
        useCase.getSchoolSupply(
            rfid = rfidCode1,
            success = {
                it shouldBe bookCopy1
            },
            error = {
                assert(false)
            }
        )
    }

    "Try get a School Supplies not in Desktop" {
        val desktop = Desktop.create(
            schoolSupplies = setOf(bookCopy1),
            schoolSuppliesInBackpack = setOf()
        )
        val useCase = DesktopUseCase(repository)
        coEvery {
            repository.getDesktop(any(), error = any())
        } answers {
            firstArg<(Desktop) -> Unit>().invoke(desktop)
        }
        useCase.getSchoolSupply(
            rfid = rfidCode2,
            success = {
                it shouldBe null
            },
            error = {
                assert(false)
            }
        )
    }

    "Subscribe to backpack" {
        val desktop = Desktop.create(
            schoolSupplies = setOf(bookCopy1, bookCopy2),
            schoolSuppliesInBackpack = setOf()
        )
        val useCase = DesktopUseCase(repository)
        val desktopSlot = slot<(Desktop) -> Unit>()
        val order: List<Set<SchoolSupply>> =
            listOf(setOf(bookCopy1), setOf(), setOf(bookCopy2), setOf(), setOf(bookCopy1, bookCopy2))
        val rfidFlow = flow {
            for (element in order) {
                delay(20)
                emit(element.map { supply -> supply.rfidCode }.toSet())
            }
        }
        coEvery {
            repository.getDesktop(capture(desktopSlot), any())
        } answers {
            desktopSlot.captured(desktop)
        }
        coEvery {
            repository.subscribeToBackpack(any(), any())
        } answers {
            firstArg<(Flow<Set<String>>) -> Unit>().invoke(
                rfidFlow
            )
        }
        useCase.subscribeToBackpack({ flow ->
            runBlocking {
                val result = flow.onEach { delay(110) }.toList()
                result shouldBe listOf(setOf(bookCopy1), setOf(bookCopy1, bookCopy2))
            }
        }, {
            assert(false)
        })
    }

    "Delete desktop" {
        val desktop = Desktop.create(
            schoolSupplies = setOf(bookCopy1, bookCopy2),
            schoolSuppliesInBackpack = setOf()
        )
        val useCase = DesktopUseCase(repository)
        val desktopSlot = slot<(Desktop) -> Unit>()
        coEvery {
            repository.getDesktop(capture(desktopSlot), any())
        } answers {
            desktopSlot.captured(desktop)
        }
        coEvery {
            repository.deleteDesktop(any(), any())
        } answers {
            firstArg<() -> Unit>().invoke()
        }
        coEvery {
            repository.getDesktop(capture(desktopSlot), any())
        } answers {
            desktopSlot.captured(Desktop.create())
        }
        useCase.deleteDesktop({
            runBlocking {
                useCase.getDesktop({
                    it.schoolSupplies shouldBe setOf()
                }, {
                    assert(false)
                })
            }
        }, {
            assert(false)
        })
    }
})
