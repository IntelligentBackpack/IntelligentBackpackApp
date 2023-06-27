package com.intelligentbackpack.desktopdomain

import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.accessdomain.usecase.AccessUseCase
import com.intelligentbackpack.desktopdomain.entities.Book
import com.intelligentbackpack.desktopdomain.entities.BookCopy
import com.intelligentbackpack.desktopdomain.entities.Desktop
import com.intelligentbackpack.desktopdomain.entities.SchoolSupply
import com.intelligentbackpack.desktopdomain.exception.ISBNException
import com.intelligentbackpack.desktopdomain.repository.DesktopDomainRepository
import com.intelligentbackpack.desktopdomain.usecase.DesktopUseCase
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
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
    val backpack = "Backpack"
    val repository = mockk<DesktopDomainRepository>(relaxed = true)
    val accessUseCase = mockk<AccessUseCase>(relaxed = true)

    "Get Desktop" {
        val desktop = Desktop.create(
            schoolSupplies = setOf(bookCopy1),
            schoolSuppliesInBackpack = setOf(),
        )
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(user)
        val useCase = DesktopUseCase(accessUseCase, repository)
        coEvery { repository.getDesktop(any()) } returns (desktop)
        val result = useCase.getDesktop()
        println(result)
        result.isSuccess shouldBe true
        result.getOrNull() shouldBe desktop
    }

    "Add a School Supply to Desktop" {
        val desktop = Desktop.create(
            schoolSupplies = setOf(bookCopy1),
            schoolSuppliesInBackpack = setOf(),
        )
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(user)
        val useCase = DesktopUseCase(accessUseCase, repository)
        coEvery {
            repository.getDesktop(any())
        } returns (desktop)
        coEvery {
            repository.addSchoolSupply(
                user = any(),
                schoolSupply = any(),
            )
        } returns (desktop.schoolSupplies + bookCopy2)
        useCase.addSchoolSupply(schoolSupply = bookCopy2)
            .getOrNull()!!.schoolSupplies shouldBe desktop.schoolSupplies + bookCopy2
    }

    "Get School Supplies in Desktop" {
        val desktop = Desktop.create(
            schoolSupplies = setOf(bookCopy1),
            schoolSuppliesInBackpack = setOf(),
        )
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(user)
        val useCase = DesktopUseCase(accessUseCase, repository)
        coEvery {
            repository.getDesktop(any())
        } returns (desktop)
        useCase.getSchoolSupply(rfid = rfidCode1).getOrNull() shouldBe bookCopy1
    }

    "Try get a School Supplies not in Desktop" {
        val desktop = Desktop.create(
            schoolSupplies = setOf(bookCopy1),
            schoolSuppliesInBackpack = setOf(),
        )
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(user)
        val useCase = DesktopUseCase(accessUseCase, repository)
        coEvery {
            repository.getDesktop(any())
        } returns (desktop)
        useCase.getSchoolSupply(rfid = rfidCode2).getOrDefault(bookCopy1) shouldBe null
    }

    "Subscribe to backpack" {
        val desktop = Desktop.create(
            schoolSupplies = setOf(bookCopy1, bookCopy2),
            schoolSuppliesInBackpack = setOf(),
            backpack = "backpack",
        )
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(user)
        val useCase = DesktopUseCase(accessUseCase, repository)
        val order: List<Set<SchoolSupply>> =
            listOf(setOf(bookCopy1), setOf(), setOf(bookCopy2), setOf(), setOf(bookCopy1, bookCopy2))
        val rfidFlow = flow {
            for (element in order) {
                delay(10)
                emit(element.map { supply -> supply.rfidCode }.toSet())
            }
        }
        coEvery {
            repository.getDesktop(any())
        } returns (desktop)
        coEvery {
            repository.subscribeToBackpack(any())
        } answers {
            rfidFlow
        }
        useCase.subscribeToBackpack().getOrNull()?.let { flow ->
            runBlocking {
                val result = flow.onEach { delay(200) }.toList()
                result shouldBe listOf(setOf(bookCopy1), setOf(bookCopy1, bookCopy2))
            }
        }
    }

    "Delete desktop" {
        val desktop = Desktop.create(
            schoolSupplies = setOf(bookCopy1, bookCopy2),
            schoolSuppliesInBackpack = setOf(),
        )
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(user)
        val useCase = DesktopUseCase(accessUseCase, repository)
        coEvery {
            repository.getDesktop(any())
        } returns (desktop)
        useCase.getDesktop().getOrNull() shouldBe desktop
        coEvery {
            repository.logoutDesktop(any())
        } answers {}
        coEvery {
            repository.getDesktop(any())
        } returns (Desktop.create())
        useCase.logoutDesktop().getOrNull()
        useCase.getDesktop().getOrNull()!!.schoolSupplies shouldBe emptySet()
    }

    "Associate backpack" {
        val desktop = Desktop.create(
            schoolSupplies = setOf(bookCopy1, bookCopy2),
            schoolSuppliesInBackpack = setOf(),
        )
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(user)
        val useCase = DesktopUseCase(accessUseCase, repository)
        coEvery {
            repository.getDesktop(any())
        } returns (desktop)
        coEvery {
            repository.associateBackpack(any(), any())
        } returns (backpack)
        val result = useCase.associateBackpack(backpack)
        result.isSuccess shouldBe true
        result.getOrNull()!!.isBackpackAssociated shouldBe true
    }

    "Associate backpack with error" {
        val desktop = Desktop.create(
            schoolSupplies = setOf(bookCopy1, bookCopy2),
            schoolSuppliesInBackpack = setOf(),
        )
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(user)
        val useCase = DesktopUseCase(accessUseCase, repository)
        coEvery {
            repository.getDesktop(any())
        } returns (desktop)
        coEvery {
            repository.associateBackpack(any(), any())
        } throws (Exception())
        val result = useCase.associateBackpack(backpack)
        result.isFailure shouldBe true
        desktop.isBackpackAssociated shouldBe false
    }

    "Disassociate backpack" {
        val desktop = Desktop.create(
            schoolSupplies = setOf(bookCopy1, bookCopy2),
            schoolSuppliesInBackpack = setOf(),
            backpack = backpack,
        )
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(user)
        val useCase = DesktopUseCase(accessUseCase, repository)
        coEvery {
            repository.getDesktop(any())
        } returns (desktop)
        coEvery {
            repository.disassociateBackpack(any(), any())
        } returns (backpack)
        val result = useCase.disassociateBackpack(backpack)
        result.isSuccess shouldBe true
        result.getOrNull()!!.isBackpackAssociated shouldBe false
    }

    "Disassociate backpack with error" {
        val desktop = Desktop.create(
            schoolSupplies = setOf(bookCopy1, bookCopy2),
            schoolSuppliesInBackpack = setOf(),
            backpack = backpack,
        )
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(user)
        val useCase = DesktopUseCase(accessUseCase, repository)
        coEvery {
            repository.getDesktop(any())
        } returns (desktop)
        coEvery {
            repository.disassociateBackpack(any(), any())
        } throws (Exception())
        useCase.disassociateBackpack(backpack).getOrNull()
        desktop.isBackpackAssociated shouldBe true
    }

    "Get a book copy from its isbn" {
        val isbn2 = "9788843025336"
        val book2 = Book.build {
            this.isbn = isbn2
            this.title = title
            this.authors = authors
        }
        val bookCopy3 = BookCopy.build {
            this.book = book2
            this.rfidCode = rfidCode2
        }
        val desktop = Desktop.create(
            schoolSupplies = setOf(bookCopy1, bookCopy3),
            schoolSuppliesInBackpack = setOf(),
            backpack = backpack,
        )
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(user)
        val useCase = DesktopUseCase(accessUseCase, repository)

        coEvery {
            repository.getDesktop(any())
        } returns (desktop)
        useCase.getBookCopy(isbn = isbn2).getOrNull() shouldBe bookCopy3
    }

    "should return null if the book copy is not found" {
        val isbn2 = "9788843025336"
        val desktop = Desktop.create(
            schoolSupplies = setOf(bookCopy1),
            schoolSuppliesInBackpack = setOf(),
            backpack = backpack,
        )
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(user)
        val useCase = DesktopUseCase(accessUseCase, repository)

        coEvery {
            repository.getDesktop(any())
        } returns (desktop)
        val result = useCase.getBookCopy(isbn = isbn2)
        result.isSuccess shouldBe true
        result.getOrNull() shouldBe null
    }

    "should return error if the isbn isn't in the right format" {
        val desktop = Desktop.create(
            schoolSupplies = setOf(bookCopy1),
            schoolSuppliesInBackpack = setOf(),
            backpack = backpack,
        )
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(user)
        val useCase = DesktopUseCase(accessUseCase, repository)

        coEvery {
            repository.getDesktop(any())
        } returns (desktop)
        val result = useCase.getBookCopy(isbn = "9788843025344")
        result.isFailure shouldBe true
        result.exceptionOrNull() shouldBe ISBNException()
    }
})
