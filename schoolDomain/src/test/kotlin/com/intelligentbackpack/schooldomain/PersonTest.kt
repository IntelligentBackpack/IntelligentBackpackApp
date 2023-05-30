package com.intelligentbackpack.schooldomain

import com.intelligentbackpack.schooldomain.entities.Class
import com.intelligentbackpack.schooldomain.entities.School
import com.intelligentbackpack.schooldomain.entities.Subject
import com.intelligentbackpack.schooldomain.entities.person.Professor
import io.kotest.core.spec.style.StringSpec
import com.intelligentbackpack.schooldomain.entities.person.Student
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe

class PersonTest : StringSpec({

    val school = School.create("ITI L. Da Vinci", "Rimini")
    val studentClass = Class.create("1A", school)
    val email = "test@gmail.com"
    val name = "John"
    val surname = "Doe"
    val math: Subject = "Math"

    "should be able to create a Student" {
        val student = Student.create(email, name, surname, studentClass)
        student.email shouldBe email
        student.name shouldBe name
        student.surname shouldBe surname
        student.studentClass?.name shouldBe studentClass.name
    }

    "should have a error when creating a Student with blank email" {
        val exception = shouldThrow<IllegalArgumentException> {
            Student.create("", name, surname, studentClass)
        }
        exception.message shouldBe "email cannot be blank"
    }

    "should have a error when creating a Student with blank name" {
        val exception = shouldThrow<IllegalArgumentException> {
            Student.create(email, "", surname, studentClass)
        }
        exception.message shouldBe "name cannot be blank"
    }

    "should have a error when creating a Student with blank surname" {
        val exception = shouldThrow<IllegalArgumentException> {
            Student.create(email, name, "", studentClass)
        }
        exception.message shouldBe "surname cannot be blank"
    }

    "should be able to create a Professor" {
        val professor = Professor.create(email, name, surname, mapOf(studentClass to setOf(math)))
        professor.email shouldBe email
        professor.name shouldBe name
        professor.surname shouldBe surname
        professor.professorClasses shouldBe setOf(studentClass)
        professor.professorSubjects shouldBe setOf(math)
    }

    "should have a error when creating a Professor with blank email" {
        val exception = shouldThrow<IllegalArgumentException> {
            Professor.create("", name, surname, mapOf(studentClass to setOf(math)))
        }
        exception.message shouldBe "email cannot be blank"
    }

    "should have a error when creating a Professor with blank name" {
        val exception = shouldThrow<IllegalArgumentException> {
            Professor.create(email, "", surname, mapOf(studentClass to setOf(math)))
        }
        exception.message shouldBe "name cannot be blank"
    }

    "should have a error when creating a Professor with blank surname" {
        val exception = shouldThrow<IllegalArgumentException> {
            Professor.create(email, name, "", mapOf(studentClass to setOf(math)))
        }
        exception.message shouldBe "surname cannot be blank"
    }

    "should have a error when creating a Professor with a class without subjects" {
        val exception = shouldThrow<IllegalArgumentException> {
            Professor.create(email, name, surname, mapOf(studentClass to setOf()))
        }
        exception.message shouldBe "professorClasses cannot be empty"
    }

    "should be able to add a subject to a Professor" {
        val professor = Professor.create(email, name, surname, mapOf(studentClass to setOf(math)))
        val newSubject: Subject = "English"
        val newProfessor = professor.addProfessorToClass(studentClass, setOf(newSubject))
        newProfessor.professorSubjects shouldBe setOf(math, newSubject)
    }

    "should have a error when adding an empty set of subject to a professor in class" {
        val professor = Professor.create(email, name, surname, mapOf(studentClass to setOf(math)))
        val exception = shouldThrow<IllegalArgumentException> {
            professor.addProfessorToClass(studentClass, setOf())
        }
        exception.message shouldBe "subjects cannot be empty"
    }
})
