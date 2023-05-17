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
        useCase.createUser(user = user, success = {}, error = {})
        coVerify { repository.createUser(capture(userCapture)) }
        userCapture.captured shouldBe user
    }

    "Login a user with email and password" {
        val useCase = AccessUseCase(repository)
        val emailCaptor = slot<String>()
        val passwordCaptor = slot<String>()
        useCase.loginWithData(user.email, user.password, success = {}, error = {})
        coVerify { repository.loginWithData(capture(emailCaptor), capture(passwordCaptor)) }
        emailCaptor.captured shouldBe user.email
        passwordCaptor.captured shouldBe user.password
    }

    "Login automatically a user" {
        val useCase = AccessUseCase(repository)
        coEvery {
            repository.loginWithData(any(), any())
        } returns user
        useCase.loginWithData(user.email, user.password, success = {}, error = {})
        coEvery { repository.automaticLogin() } returns user
        useCase.automaticLogin(success = {
            it shouldBe user
        }, error = { assert(false) })
    }
})
