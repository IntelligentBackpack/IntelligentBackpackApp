package com.intelligentbackpack.accessdomain

import com.intelligentbackpack.accessdomain.entities.Role
import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.accessdomain.exceptions.InvalidEmailException
import com.intelligentbackpack.accessdomain.exceptions.InvalidPasswordException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.lang.IllegalArgumentException

class UserTests : StringSpec({
    "Create a user" {
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
        user.email shouldBe email
        user.name shouldBe name
        user.surname shouldBe surname
        user.password shouldBe password
        user.role shouldBe role
    }

    "Create a user with default role" {
        val email = "test@mail.com"
        val name = "Test"
        val surname = "Test"
        val password = "Test#1234"
        val user = User.build {
            this.email = email
            this.name = name
            this.surname = surname
            this.password = password
        }
        user.email shouldBe email
        user.name shouldBe name
        user.surname shouldBe surname
        user.password shouldBe password
        user.role shouldBe Role.USER
    }

    "Bad email format, no @ symbol" {
        val email = "test"
        val name = "Test"
        val surname = "Test"
        val password = "test"
        shouldThrow<InvalidEmailException> {
            User.build {
                this.email = email
                this.name = name
                this.surname = surname
                this.password = password
            }
        }
    }

    "Bad email format, no domain" {
        val email = "test@"
        val name = "Test"
        val surname = "Test"
        val password = "test"
        shouldThrow<InvalidEmailException> {
            User.build {
                this.email = email
                this.name = name
                this.surname = surname
                this.password = password
            }
        }
    }

    "Bad email format, no domain name" {
        val email = "test@."
        val name = "Test"
        val surname = "Test"
        val password = "test"
        shouldThrow<InvalidEmailException> {
            User.build {
                this.email = email
                this.name = name
                this.surname = surname
                this.password = password
            }
        }
    }

    "Bad email format, no domain extension" {
        val email = "test@mail"
        val name = "Test"
        val surname = "Test"
        val password = "test"
        shouldThrow<InvalidEmailException> {
            User.build {
                this.email = email
                this.name = name
                this.surname = surname
                this.password = password
            }
        }
    }

    "Bad email format, no domain extension after dot" {
        val email = "test@mail."
        val name = "Test"
        val surname = "Test"
        val password = "test"
        shouldThrow<InvalidEmailException> {
            User.build {
                this.email = email
                this.name = name
                this.surname = surname
                this.password = password
            }
        }
    }

    "Bad password format, no uppercase" {
        val email = "test@mail.com"
        val name = "Test"
        val surname = "Test"
        val password = "test#1234"
        shouldThrow<InvalidPasswordException> {
            User.build {
                this.email = email
                this.name = name
                this.surname = surname
                this.password = password
            }
        }
    }

    "Bad password format, no lowercase" {
        val email = "test@mail.com"
        val name = "Test"
        val surname = "Test"
        val password = "TEST#1234"
        shouldThrow<InvalidPasswordException> {
            User.build {
                this.email = email
                this.name = name
                this.surname = surname
                this.password = password
            }
        }
    }

    "Bad password format, no number" {
        val email = "test@mail.com"
        val name = "Test"
        val surname = "Test"
        val password = "Test#"
        shouldThrow<InvalidPasswordException> {
            User.build {
                this.email = email
                this.name = name
                this.surname = surname
                this.password = password
            }
        }
    }

    "Bad password format, no special character" {
        val email = "test@mail.com"
        val name = "Test"
        val surname = "Test"
        val password = "Test1234"
        shouldThrow<InvalidPasswordException> {
            User.build {
                this.email = email
                this.name = name
                this.surname = surname
                this.password = password
            }
        }
    }

    "Bad password format, not enough characters" {
        val email = "test@mail.com"
        val name = "Test"
        val surname = "Test"
        val password = "Te#1"
        shouldThrow<InvalidPasswordException> {
            User.build {
                this.email = email
                this.name = name
                this.surname = surname
                this.password = password
            }
        }
    }

    "Bad name format, empty" {
        val email = "test@mail.com"
        val name = ""
        val surname = "Test"
        val password = "Test#1234"
        shouldThrow<IllegalArgumentException> {
            User.build {
                this.email = email
                this.name = name
                this.surname = surname
                this.password = password
            }
        }
    }

    "Bad surname format, empty" {
        val email = "test@mail.com"
        val name = "Test"
        val surname = ""
        val password = "Test#1234"
        shouldThrow<IllegalArgumentException> {
            User.build {
                this.email = email
                this.name = name
                this.surname = surname
                this.password = password
            }
        }
    }

    "Always first letter of name and surname uppercase" {
        val email = "test@mail.com"
        val name = "test"
        val surname = "test"
        val password = "Test#1234"
        val user = User.build {
            this.email = email
            this.name = name
            this.surname = surname
            this.password = password
        }
        user.name shouldBe "Test"
        user.surname shouldBe "Test"
    }

    "Remove whitespaces from name and surname" {
        val email = "test@mail.com"
        val name = " test"
        val surname = " test"
        val password = "Test#1234"
        val user = User.build {
            this.email = email
            this.name = name
            this.surname = surname
            this.password = password
        }
        user.name shouldBe "Test"
        user.surname shouldBe "Test"
    }

    "Remove whitespaces from email" {
        val email = " test@mail.com"
        val name = " test"
        val surname = " test"
        val password = "Test#1234"
        val user = User.build {
            this.email = email
            this.name = name
            this.surname = surname
            this.password = password
        }
        user.email shouldBe "test@mail.com"
    }
})
