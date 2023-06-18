package com.intelligentbackpack.schooldomain

import com.intelligentbackpack.schooldomain.entities.person.Professor
import com.intelligentbackpack.schooldomain.entities.person.Student
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class PersonTest : StringSpec({

    val email = "test@gmail.com"
    val name = "John"
    val surname = "Doe"

    "should be able to create a Student" {
        val student = Student.create(email, name, surname)
        student.email shouldBe email
        student.name shouldBe name
        student.surname shouldBe surname
    }

    "should have a error when creating a Student with blank email" {
        val exception = shouldThrow<IllegalArgumentException> {
            Student.create("", name, surname)
        }
        exception.message shouldBe "email cannot be blank"
    }

    "should have a error when creating a Student with blank name" {
        val exception = shouldThrow<IllegalArgumentException> {
            Student.create(email, "", surname)
        }
        exception.message shouldBe "name cannot be blank"
    }

    "should have a error when creating a Student with blank surname" {
        val exception = shouldThrow<IllegalArgumentException> {
            Student.create(email, name, "")
        }
        exception.message shouldBe "surname cannot be blank"
    }

    "should be able to create a Professor" {
        val professor = Professor.create(email, name, surname)
        professor.email shouldBe email
        professor.name shouldBe name
        professor.surname shouldBe surname
    }

    "should have a error when creating a Professor with blank email" {
        val exception = shouldThrow<IllegalArgumentException> {
            Professor.create("", name, surname)
        }
        exception.message shouldBe "email cannot be blank"
    }

    "should have a error when creating a Professor with blank name" {
        val exception = shouldThrow<IllegalArgumentException> {
            Professor.create(email, "", surname)
        }
        exception.message shouldBe "name cannot be blank"
    }

    "should have a error when creating a Professor with blank surname" {
        val exception = shouldThrow<IllegalArgumentException> {
            Professor.create(email, name, "")
        }
        exception.message shouldBe "surname cannot be blank"
    }
})
