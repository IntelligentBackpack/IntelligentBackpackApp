package com.intelligentbackpack.accessdomain

import com.intelligentbackpack.accessdomain.entities.Role
import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.accessdomain.repository.AccessDomainRepository
import com.intelligentbackpack.accessdomain.usecase.AccessUseCase
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot

class UseCaseTest : StringSpec({

    val email = "test@mail.com"
    val name = "Test"
    val surname = "Test"
    val password = "Test#1234"
    val role = Role.STUDENT
    val user = User.build {
        this.email = email
        this.name = name
        this.surname = surname
        this.password = password
        this.role = role
    }
    val repository = mockk<AccessDomainRepository>(relaxed = true)

    "Create a user" {
        val useCase = AccessUseCase(repository)
        val userCapture = slot<User>()
        val result = useCase.createUser(user = user)
        coVerify { repository.createUser(capture(userCapture)) }
        userCapture.captured shouldBe user
        result.isSuccess shouldBe true
    }

    "Login a user with email and password" {
        val useCase = AccessUseCase(repository)
        val emailCaptor = slot<String>()
        val passwordCaptor = slot<String>()
        val result = useCase.loginWithData(user.email, user.password)
        coVerify { repository.loginWithData(capture(emailCaptor), capture(passwordCaptor)) }
        emailCaptor.captured shouldBe user.email
        passwordCaptor.captured shouldBe user.password
        result.isSuccess shouldBe true
    }

    "Login automatically a user" {
        val useCase = AccessUseCase(repository)
        coEvery { repository.loginWithData(any(), any()) } returns user
        useCase.loginWithData(user.email, user.password)
        coEvery { repository.automaticLogin() } returns user
        val result = useCase.automaticLogin()
        result.isSuccess shouldBe true
        result.getOrNull() shouldBe user
    }
})
