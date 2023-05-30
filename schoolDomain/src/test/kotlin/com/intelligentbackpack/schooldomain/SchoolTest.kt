package com.intelligentbackpack.schooldomain

import com.intelligentbackpack.schooldomain.entities.School
import com.intelligentbackpack.schooldomain.entities.Class
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
        val school = School.create(schoolName, schoolCity)
        val studentClass = Class.create(class1A, school)
        studentClass.name shouldBe class1A
        studentClass.school shouldBe school
    }

    "should have a error when creating a Class with blank name" {
        val school = School.create(schoolName, schoolCity)
        val exception = shouldThrow<IllegalArgumentException> {
            Class.create("", school)
        }
        exception.message shouldBe "name cannot be blank"
    }

    "should add a student to a class" {
        val school = School.create(schoolName, schoolCity)
        val studentClass = Class.create(class1A, school)
        val student = Student.create(email, name, surname, studentClass)
        val newStudentClass = studentClass.addStudent(student)
        student.studentClass shouldBe studentClass
        newStudentClass.students shouldBe setOf(student)
    }

    "should have a error when adding a student to a class that is not his" {
        val school = School.create(schoolName, schoolCity)
        val studentClass = Class.create(class1A, school)
        val student = Student.create(email, name, surname, studentClass)
        val studentClass2 = Class.create("2A", school)
        val exception = shouldThrow<IllegalArgumentException> {
            studentClass2.addStudent(student)
        }
        exception.message shouldBe "student is not in this class"
    }

    "should have a error when adding a student to a class in which he is already in" {
        val school = School.create(schoolName, schoolCity)
        val studentClass = Class.create(class1A, school)
        val student = Student.create(email, name, surname, studentClass)
        val newStudentClass = studentClass.addStudent(student)
        val exception = shouldThrow<IllegalArgumentException> {
            newStudentClass.addStudent(student)
        }
        exception.message shouldBe "student is already in this class"
    }

    "should have a error when adding a student to a class that is not in the same school" {
        val school = School.create(schoolName, schoolCity)
        val studentClass = Class.create(class1A, school)
        val student = Student.create(email, name, surname, studentClass)
        val school2 = School.create("Liceo A. Einstein", "Rimini")
        val studentClass2 = Class.create(class1A, school2)
        val exception = shouldThrow<IllegalArgumentException> {
            studentClass2.addStudent(student)
        }
        exception.message shouldBe "student is not in this class"
    }

    "should add a professor to a class" {
        val school = School.create(schoolName, schoolCity)
        val studentClass = Class.create(class1A, school)
        val professor = Professor.create(email, name, surname, mapOf(studentClass to setOf("Math", "Physics")))
        val result = studentClass.addProfessor(professor, setOf("Math", "Physics"))
        val newStudentClass = result.first
        val newProfessor = result.second
        newStudentClass.professors shouldBe setOf(professor)
        newStudentClass.professorTeachSubjects[professor] shouldBe setOf("Math", "Physics")
        newProfessor.professorClasses shouldBe setOf(newStudentClass)
        newProfessor.professorSubjectsInClasses shouldBe mapOf(newStudentClass to setOf("Math", "Physics"))
    }

    "should be able to add a professor to a class even when the class isn't his" {
        val school = School.create(schoolName, schoolCity)
        val studentClass = Class.create(class1A, school)
        val professor = Professor.create(email, name, surname, mapOf(studentClass to setOf("Math", "Physics")))
        val studentClass2 = Class.create("2A", school)
        val result = studentClass2.addProfessor(professor, setOf("Math", "Physics"))
        val newProfessor = result.second
        newProfessor.professorClasses shouldBe setOf(studentClass, studentClass2)
    }

    "should have a error when adding a professor to a class that is not in the same school" {
        val school = School.create(schoolName, schoolCity)
        val studentClass = Class.create(class1A, school)
        val professor = Professor.create(email, name, surname, mapOf(studentClass to setOf("Math", "Physics")))
        val school2 = School.create("Liceo A. Einstein", "Rimini")
        val studentClass2 = Class.create(class1A, school2)
        val exception = shouldThrow<IllegalArgumentException> {
            studentClass2.addProfessor(professor, setOf("Math", "Physics"))
        }
        exception.message shouldBe "professor doesn't teach the subjects in this school"
    }

    "should have a error when adding a professor to a class with no subjects" {
        val school = School.create(schoolName, schoolCity)
        val studentClass = Class.create(class1A, school)
        val professor = Professor.create(email, name, surname, mapOf(studentClass to setOf("Math")))
        val exception = shouldThrow<IllegalArgumentException> {
            studentClass.addProfessor(professor, setOf())
        }
        exception.message shouldBe "subjects cannot be empty"
    }

    "should update subject when adding a professor to a class in which he is already in" {
        val school = School.create(schoolName, schoolCity)
        val studentClass = Class.create(class1A, school)
        val professor = Professor.create(email, name, surname, mapOf(studentClass to setOf("Math")))
        studentClass.addProfessor(professor, setOf("Math"))
        professor.addProfessorToClass(studentClass, setOf("Physics"))
        studentClass.addProfessor(professor, setOf("Physics"))
    }
})
