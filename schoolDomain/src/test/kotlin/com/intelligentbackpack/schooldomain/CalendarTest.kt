package com.intelligentbackpack.schooldomain

import com.intelligentbackpack.schooldomain.entities.Class
import com.intelligentbackpack.schooldomain.entities.School
import com.intelligentbackpack.schooldomain.entities.calendar.CalendarEventFactory.createWeekLesson
import com.intelligentbackpack.schooldomain.entities.calendar.SchoolCalendar
import com.intelligentbackpack.schooldomain.entities.person.Professor
import com.intelligentbackpack.schooldomain.exceptions.EventOverlappingException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class CalendarTest : StringSpec({

    val schoolYear = "2022-2023"
    val schoolName = "ITI L. Da Vinci"
    val schoolCity = "Rimini"
    val class1A = "1A"
    val email = "test@gmail.com"
    val name = "John"
    val surname = "Doe"
    val math = "Math"
    val physics = "Physics"
    val calendar = SchoolCalendar.create(schoolYear)
    val school = School.create(schoolName, schoolCity).replaceCalendar(calendar)
    val studentClass = Class.create(class1A, school)
    val professor = Professor.create(email, name, surname, mapOf(studentClass to setOf(math, physics)))
    val singleMondayLesson = createWeekLesson(
        day = DayOfWeek.MONDAY,
        subject = math,
        startTime = LocalTime.of(8, 30),
        endTime = LocalTime.of(9, 30),
        professor = professor,
        fromDate = LocalDate.of(2022, 9, 12),
        toDate = LocalDate.of(2022, 12, 23),
        studentsClass = studentClass,
    )
    val mondayLessons = setOf(
        singleMondayLesson,
        createWeekLesson(
            day = DayOfWeek.MONDAY,
            subject = physics,
            startTime = LocalTime.of(9, 30),
            endTime = LocalTime.of(10, 30),
            professor = professor,
            fromDate = LocalDate.of(2022, 9, 12),
            toDate = LocalDate.of(2022, 12, 23),
            studentsClass = studentClass,
        ),
    )

    val tuesday = setOf(
        createWeekLesson(
            day = DayOfWeek.TUESDAY,
            subject = math,
            startTime = LocalTime.of(10, 30),
            endTime = LocalTime.of(11, 30),
            professor = professor,
            fromDate = LocalDate.of(2022, 9, 12),
            toDate = LocalDate.of(2022, 12, 23),
            studentsClass = studentClass,
        ),
    )

    "should be able to create a Calendar" {
        calendar.schoolYear shouldBe schoolYear
        calendar.alterations shouldBe emptySet()
        calendar.studentsTimeTableLesson shouldBe emptyMap()
        calendar.professorsTimeTableLesson shouldBe emptyMap()
    }

    "should be able to add a student time table lesson" {
        val lessons = setOf(singleMondayLesson)
        val newCalendar = calendar.addLessons(lessons)
        newCalendar.studentsTimeTableLesson shouldBe mapOf(studentClass to mapOf(DayOfWeek.MONDAY to lessons.toList()))
    }

    "should be able to add a student time table lesson more lesson" {
        val lessons = mondayLessons + tuesday
        val newCalendar = calendar.addLessons(lessons)
        newCalendar.studentsTimeTableLesson shouldBe mapOf(
            studentClass to mapOf(
                DayOfWeek.MONDAY to mondayLessons.toList(),
                DayOfWeek.TUESDAY to tuesday.toList(),
            ),
        )
    }

    "should be able to add a professor time table lesson" {
        val lessons = setOf(singleMondayLesson)
        val newCalendar = calendar.addLessons(lessons)
        newCalendar.professorsTimeTableLesson shouldBe mapOf(professor to mapOf(DayOfWeek.MONDAY to lessons.toList()))
    }

    "should have an error if a professor doesn't teach a subject" {
        val exception = shouldThrow<IllegalArgumentException> {
            createWeekLesson(
                day = DayOfWeek.MONDAY,
                subject = "Italian",
                startTime = LocalTime.of(8, 30),
                endTime = LocalTime.of(9, 30),
                professor = professor,
                fromDate = LocalDate.of(2022, 9, 12),
                toDate = LocalDate.of(2022, 12, 23),
                studentsClass = studentClass,
            )
        }
        exception.message shouldBe "professor must teach subject"
    }

    "should have an error if fromDate is after toDate" {
        val exception = shouldThrow<IllegalArgumentException> {
            createWeekLesson(
                day = DayOfWeek.MONDAY,
                subject = math,
                startTime = LocalTime.of(8, 30),
                endTime = LocalTime.of(9, 30),
                professor = professor,
                fromDate = LocalDate.of(2022, 12, 23),
                toDate = LocalDate.of(2022, 9, 12),
                studentsClass = studentClass,
            )
        }
        exception.message shouldBe "fromDate cannot be after toDate"
    }

    "should have an error if a professor teaches more than one subject at the same time" {
        val studentClass2 = Class.create("2A", school)
        shouldThrow<EventOverlappingException> {
            val lessons = setOf(
                singleMondayLesson,
                createWeekLesson(
                    day = DayOfWeek.MONDAY,
                    subject = physics,
                    startTime = LocalTime.of(8, 30),
                    endTime = LocalTime.of(10, 30),
                    professor = professor,
                    fromDate = LocalDate.of(2022, 9, 12),
                    toDate = LocalDate.of(2022, 12, 23),
                    studentsClass = studentClass2,
                ),
            )
            calendar.addLessons(lessons)
        }
    }

    "should have an error if a student has more than one lesson at the same time" {
        shouldThrow<EventOverlappingException> {
            val lessons = setOf(
                singleMondayLesson,
                createWeekLesson(
                    day = DayOfWeek.MONDAY,
                    subject = physics,
                    startTime = LocalTime.of(8, 30),
                    endTime = LocalTime.of(10, 30),
                    professor = professor,
                    fromDate = LocalDate.of(2022, 9, 11),
                    toDate = LocalDate.of(2022, 10, 23),
                    studentsClass = studentClass,
                ),
            )
            calendar.addLessons(lessons)
        }
    }

    "should be able to have a student's lessons for a day" {
        val lessons = mondayLessons + tuesday
        val newCalendar = calendar.addLessons(lessons)
        val day = LocalDate.of(2022, 9, 12)
        day.dayOfWeek shouldBe DayOfWeek.MONDAY
        newCalendar.getStudentsEvents(studentClass, day) shouldBe mondayLessons
    }

    "should be able to have a professor's lessons for a day" {
        val lessons = mondayLessons + tuesday
        val newCalendar = calendar.addLessons(lessons)
        val day = LocalDate.of(2022, 9, 12)
        day.dayOfWeek shouldBe DayOfWeek.MONDAY
        newCalendar.getProfessorEvents(professor, day) shouldBe mondayLessons
    }

    "should be able to have a an empty list of student's lessons for a day after the last lesson" {
        val lessons = mondayLessons + tuesday
        val newCalendar = calendar.addLessons(lessons)
        val day = LocalDate.of(2022, 12, 14)
        newCalendar.getStudentsEvents(studentClass, day) shouldBe emptyList()
    }

    "should be able to have a an empty list of student's lessons for a day before the first lesson" {
        val lessons = mondayLessons + tuesday
        val newCalendar = calendar.addLessons(lessons)
        val day = LocalDate.of(2022, 9, 11)
        newCalendar.getStudentsEvents(studentClass, day) shouldBe emptyList()
    }

    "should be able to have a an empty list of professor's lessons for a day after the last lesson" {
        val lessons = mondayLessons + tuesday
        val newCalendar = calendar.addLessons(lessons)
        val day = LocalDate.of(2022, 12, 14)
        newCalendar.getProfessorEvents(professor, day) shouldBe emptyList()
    }

    "should be able to have a an empty list of professor's lessons for a day before the first lesson" {
        val lessons = mondayLessons + tuesday
        val newCalendar = calendar.addLessons(lessons)
        val day = LocalDate.of(2022, 9, 11)
        newCalendar.getProfessorEvents(professor, day) shouldBe emptyList()
    }

    "should be able to have a student's lessons ordered by time" {
        val lessons = (mondayLessons.toList().sortedByDescending { it.startTime } + tuesday).toSet()
        val newCalendar = calendar.addLessons(lessons)
        val day = LocalDate.of(2022, 9, 12)
        day.dayOfWeek shouldBe DayOfWeek.MONDAY
        newCalendar.getStudentsEvents(studentClass, day) shouldBe mondayLessons.toList().sortedBy { it.startTime }
    }
})
