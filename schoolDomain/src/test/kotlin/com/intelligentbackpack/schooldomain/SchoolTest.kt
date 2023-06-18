package com.intelligentbackpack.schooldomain

import com.intelligentbackpack.schooldomain.entities.Class
import com.intelligentbackpack.schooldomain.entities.School
import com.intelligentbackpack.schooldomain.entities.person.Professor
import com.intelligentbackpack.schooldomain.entities.person.Student
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SchoolTest : StringSpec({

    val schoolName = "ITI L. Da Vinci"
    val schoolCity = "Rimini"
    val class1A = "1A"
    val email = "test@gmail.com"
    val name = "John"
    val surname = "Doe"

    "should be able to create a School" {
        val school = School.create(schoolName, schoolCity)
        school.name shouldBe schoolName
        school.city shouldBe schoolCity
    }

    "should have a error when creating a School with blank name" {
        val exception = shouldThrow<IllegalArgumentException> {
            School.create("", schoolCity)
        }
        exception.message shouldBe "name cannot be blank"
    }

    "should have a error when creating a School with blank city" {
        val exception = shouldThrow<IllegalArgumentException> {
            School.create(schoolName, "")
        }
        exception.message shouldBe "city cannot be blank"
    }

    "should be able to create a Class" {
        val studentClass = Class.create(class1A)
        studentClass.name shouldBe class1A
    }

    "should have a error when creating a Class with blank name" {
        val exception = shouldThrow<IllegalArgumentException> {
            Class.create("")
        }
        exception.message shouldBe "name cannot be blank"
    }

    "should add a student to a class" {
        val studentClass = Class.create(class1A)
        val student = Student.create(email, name, surname)
        val newStudentClass = studentClass.addStudent(student)
        newStudentClass.students shouldBe setOf(student)
    }

    "should have a error when adding a student to a class in which he is already in" {
        val studentClass = Class.create(class1A)
        val student = Student.create(email, name, surname)
        val newStudentClass = studentClass.addStudent(student)
        val exception = shouldThrow<IllegalArgumentException> {
            newStudentClass.addStudent(student)
        }
        exception.message shouldBe "student is already in this class"
    }

    "should add a professor to a class" {
        val studentClass = Class.create(class1A)
        val professor = Professor.create(email, name, surname)
        val newStudentClass = studentClass.addProfessor(professor, setOf("Math", "Physics"))
        newStudentClass.professors shouldBe setOf(professor)
        newStudentClass.professorTeachSubjects[professor] shouldBe setOf("Math", "Physics")
    }

    "should have a error when adding a professor to a class with no subjects" {
        val studentClass = Class.create(class1A)
        val professor = Professor.create(email, name, surname)
        val exception = shouldThrow<IllegalArgumentException> {
            studentClass.addProfessor(professor, setOf())
        }
        exception.message shouldBe "subjects cannot be empty"
    }
})
