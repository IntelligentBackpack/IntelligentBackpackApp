package com.intelligentbackpack.desktopdomain

import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.accessdomain.usecase.AccessUseCase
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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking

class UseCaseTest : StringSpec({

    val user = User.build {
        this.name = "test"
        this.surname = "test"
        this.email = "test@gmail.com"
        this.password = "Test#1234"
    }
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
    val accessUseCase = mockk<AccessUseCase>(relaxed = true)

    "Get Desktop" {
        val desktop = Desktop.create(
            schoolSupplies = setOf(bookCopy1),
            schoolSuppliesInBackpack = setOf()
        )
        coEvery {
            accessUseCase.automaticLogin(any(), any())
        } answers {
            firstArg<(User) -> Unit>().invoke(user)
        }
        val useCase = DesktopUseCase(accessUseCase, repository)
        val desktopSlot = slot<(Desktop) -> Unit>()
        coEvery {
            repository.getDesktop(any(), capture(desktopSlot), any())
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
        coEvery {
            accessUseCase.automaticLogin(any(), any())
        } answers {
            firstArg<(User) -> Unit>().invoke(user)
        }
        val useCase = DesktopUseCase(accessUseCase, repository)
        val schoolSupply = slot<(Set<SchoolSupply>) -> Unit>()
        val desktopSlot = slot<(Desktop) -> Unit>()
        coEvery {
            repository.getDesktop(any(), success = capture(desktopSlot), error = any())
        } answers {
            secondArg<(Desktop) -> Unit>().invoke(desktop)
        }
        coEvery {
            repository.addSchoolSupply(
                user = any(),
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

    "Get School Supplies in Desktop" {
        val desktop = Desktop.create(
            schoolSupplies = setOf(bookCopy1),
            schoolSuppliesInBackpack = setOf()
        )
        coEvery {
            accessUseCase.automaticLogin(any(), any())
        } answers {
            firstArg<(User) -> Unit>().invoke(user)
        }
        val useCase = DesktopUseCase(accessUseCase, repository)
        coEvery {
            repository.getDesktop(any(), any(), error = any())
        } answers {
            secondArg<(Desktop) -> Unit>().invoke(desktop)
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
        coEvery {
            accessUseCase.automaticLogin(any(), any())
        } answers {
            firstArg<(User) -> Unit>().invoke(user)
        }
        val useCase = DesktopUseCase(accessUseCase, repository)
        coEvery {
            repository.getDesktop(any(), any(), error = any())
        } answers {
            secondArg<(Desktop) -> Unit>().invoke(desktop)
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
        coEvery {
            accessUseCase.automaticLogin(any(), any())
        } answers {
            firstArg<(User) -> Unit>().invoke(user)
        }
        val useCase = DesktopUseCase(accessUseCase, repository)
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
            repository.getDesktop(any(), capture(desktopSlot), any())
        } answers {
            desktopSlot.captured(desktop)
        }
        coEvery {
            repository.subscribeToBackpack(any())
        } answers {
            rfidFlow
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
        coEvery {
            accessUseCase.automaticLogin(any(), any())
        } answers {
            firstArg<(User) -> Unit>().invoke(user)
        }
        val useCase = DesktopUseCase(accessUseCase, repository)
        val desktopSlot = slot<(Desktop) -> Unit>()
        coEvery {
            repository.getDesktop(any(), capture(desktopSlot), any())
        } answers {
            desktopSlot.captured(desktop)
        }
        useCase.getDesktop({
            it shouldBe desktop
        }, {
            assert(false)
        })
        coEvery {
            repository.logoutDesktop(any(), any(), any())
        } answers {
            secondArg<() -> Unit>().invoke()
        }
        coEvery {
            repository.getDesktop(any(), capture(desktopSlot), any())
        } answers {
            desktopSlot.captured(Desktop.create())
        }
        useCase.logoutDesktop({
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

    "Associate backpack" {
        val desktop = Desktop.create(
            schoolSupplies = setOf(bookCopy1, bookCopy2),
            schoolSuppliesInBackpack = setOf()
        )
        coEvery {
            accessUseCase.automaticLogin(any(), any())
        } answers {
            firstArg<(User) -> Unit>().invoke(user)
        }
        val useCase = DesktopUseCase(accessUseCase, repository)
        val desktopSlot = slot<(Desktop) -> Unit>()
        coEvery {
            repository.getDesktop(any(), capture(desktopSlot), any())
        } answers {
            desktopSlot.captured(desktop)
        }
        coEvery {
            repository.associateBackpack(any(), any(), any(), any())
        } answers {
            thirdArg<() -> Unit>().invoke()
        }
        useCase.associateBackpack("1234", {
            runBlocking {
                useCase.getDesktop({
                    it.backpackAssociated shouldBe true
                }, {
                    assert(false)
                })
            }
        }, {
            assert(false)
        })
    }

    "Associate backpack with error" {
        val desktop = Desktop.create(
            schoolSupplies = setOf(bookCopy1, bookCopy2),
            schoolSuppliesInBackpack = setOf()
        )
        coEvery {
            accessUseCase.automaticLogin(any(), any())
        } answers {
            firstArg<(User) -> Unit>().invoke(user)
        }
        val useCase = DesktopUseCase(accessUseCase, repository)
        val desktopSlot = slot<(Desktop) -> Unit>()
        coEvery {
            repository.getDesktop(any(), capture(desktopSlot), any())
        } answers {
            desktopSlot.captured(desktop)
        }
        coEvery {
            repository.associateBackpack(any(), any(), any(), any())
        } answers {
            lastArg<() -> Unit>().invoke()
        }
        useCase.associateBackpack("1234", {
            assert(false)
        }, {
            desktop.backpackAssociated shouldBe false
        })
    }
})
