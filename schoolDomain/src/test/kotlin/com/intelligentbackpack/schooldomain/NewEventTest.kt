package com.intelligentbackpack.schooldomain

import com.intelligentbackpack.schooldomain.entities.Class
import com.intelligentbackpack.schooldomain.entities.School
import com.intelligentbackpack.schooldomain.entities.calendar.CalendarEventFactory
import com.intelligentbackpack.schooldomain.entities.calendar.SchoolCalendar
import com.intelligentbackpack.schooldomain.entities.calendar.alteration.AlterationFactory
import com.intelligentbackpack.schooldomain.entities.person.Professor
import com.intelligentbackpack.schooldomain.exceptions.EventCantBeAddedException
import com.intelligentbackpack.schooldomain.exceptions.EventNotFoundException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class NewEventTest : StringSpec({

    val schoolYear = "2022-2023"
    val schoolName = "ITI L. Da Vinci"
    val schoolCity = "Rimini"
    val class1A = "1A"
    val email = "test@gmail.com"
    val name = "John"
    val surname = "Doe"
    val math = "Math"
    val physics = "Physics"
    val school = School.create(schoolName, schoolCity)
    val studentClass = Class.create(class1A, school)
    val professor = Professor.create(email, name, surname, mapOf(studentClass to setOf(math, physics)))
    val mondayLesson1 = CalendarEventFactory.createWeekLesson(
        day = DayOfWeek.MONDAY,
        subject = math,
        startTime = LocalTime.of(8, 30),
        endTime = LocalTime.of(9, 30),
        professor = professor,
        fromDate = LocalDate.of(2022, 9, 12),
        toDate = LocalDate.of(2022, 12, 23),
        studentsClass = studentClass,
    )
    val mondayLesson2 = CalendarEventFactory.createWeekLesson(
        day = DayOfWeek.MONDAY,
        subject = physics,
        startTime = LocalTime.of(9, 30),
        endTime = LocalTime.of(10, 30),
        professor = professor,
        fromDate = LocalDate.of(2022, 9, 12),
        toDate = LocalDate.of(2022, 12, 23),
        studentsClass = studentClass,
    )
    val mondayLessons = setOf(
        mondayLesson1,
        mondayLesson2,
    )

    val tuesday1 = CalendarEventFactory.createWeekLesson(
        day = DayOfWeek.TUESDAY,
        subject = math,
        startTime = LocalTime.of(10, 30),
        endTime = LocalTime.of(11, 30),
        professor = professor,
        fromDate = LocalDate.of(2022, 9, 12),
        toDate = LocalDate.of(2022, 12, 23),
        studentsClass = studentClass,
    )

    val tuesday = setOf(tuesday1)

    "should be able to add a new week event" {
        val calendar = SchoolCalendar.create(schoolYear)
        school.replaceCalendar(calendar)
        val lessons = mondayLessons + tuesday
        calendar.addLessons(lessons)
        val newLesson = CalendarEventFactory.createWeekLesson(
            day = DayOfWeek.TUESDAY,
            subject = math,
            startTime = LocalTime.of(11, 30),
            endTime = LocalTime.of(12, 30),
            professor = professor,
            fromDate = LocalDate.of(2022, 9, 12),
            toDate = LocalDate.of(2022, 10, 23),
            studentsClass = studentClass,
        )
        val newEvent = AlterationFactory.createNewEvent(newLesson)
        calendar.addAlteration(newEvent)
        val tuesdayInInterval = LocalDate.of(2022, 10, 18)
        tuesdayInInterval.dayOfWeek shouldBe DayOfWeek.TUESDAY
        calendar.getStudentsEvents(studentClass, tuesdayInInterval) shouldBe (tuesday + newLesson)
        calendar.getProfessorEvents(professor, tuesdayInInterval) shouldBe (tuesday + newLesson)
        val tuesdayAfterInterval = LocalDate.of(2022, 10, 25)
        tuesdayAfterInterval.dayOfWeek shouldBe DayOfWeek.TUESDAY
        calendar.getStudentsEvents(studentClass, tuesdayAfterInterval) shouldBe tuesday
        calendar.getProfessorEvents(professor, tuesdayAfterInterval) shouldBe tuesday
    }

    "should have an error if the new week event is overlapping with another event" {
        val calendar = SchoolCalendar.create(schoolYear)
        school.replaceCalendar(calendar)
        val lessons = mondayLessons + tuesday
        calendar.addLessons(lessons)
        shouldThrow<EventNotFoundException> {
            val anotherNewEvent = AlterationFactory.createNewEvent(tuesday1)
            calendar.addAlteration(anotherNewEvent)
        }
    }

    "should have an error if the new week event is overlapping with another event, smaller interval" {
        val calendar = SchoolCalendar.create(schoolYear)
        school.replaceCalendar(calendar)
        val lessons = mondayLessons + tuesday
        calendar.addLessons(lessons)
        val tuesday2 = CalendarEventFactory.createWeekLesson(
            day = DayOfWeek.TUESDAY,
            subject = math,
            startTime = LocalTime.of(10, 30),
            endTime = LocalTime.of(11, 30),
            professor = professor,
            fromDate = LocalDate.of(2022, 11, 12),
            toDate = LocalDate.of(2023, 1, 23),
            studentsClass = studentClass,
        )
        shouldThrow<EventCantBeAddedException> {
            val anotherNewEvent = AlterationFactory.createNewEvent(tuesday2)
            calendar.addAlteration(anotherNewEvent)
        }
    }

    "should be able to add a new date event" {
        val calendar = SchoolCalendar.create(schoolYear)
        school.replaceCalendar(calendar)
        val lessons = mondayLessons + tuesday
        calendar.addLessons(lessons)
        val day = LocalDate.of(2022, 10, 18)
        day.dayOfWeek shouldBe DayOfWeek.TUESDAY
        val newLesson = CalendarEventFactory.createDateLesson(
            subject = math,
            startTime = LocalTime.of(11, 30),
            endTime = LocalTime.of(12, 30),
            professor = professor,
            studentsClass = studentClass,
            date = day,
        )
        val newEvent = AlterationFactory.createNewEvent(newLesson)
        calendar.addAlteration(newEvent)
        val tuesdayInInterval = LocalDate.of(2022, 10, 18)
        tuesdayInInterval.dayOfWeek shouldBe DayOfWeek.TUESDAY
        calendar.getStudentsEvents(studentClass, tuesdayInInterval) shouldBe (tuesday + newLesson)
        calendar.getProfessorEvents(professor, tuesdayInInterval) shouldBe (tuesday + newLesson)
        val tuesdayAfterInterval = LocalDate.of(2022, 10, 25)
        tuesdayAfterInterval.dayOfWeek shouldBe DayOfWeek.TUESDAY
        calendar.getStudentsEvents(studentClass, tuesdayAfterInterval) shouldBe tuesday
        calendar.getProfessorEvents(professor, tuesdayAfterInterval) shouldBe tuesday
    }

    "should have an error if the new date event is overlapping with another event" {
        val calendar = SchoolCalendar.create(schoolYear)
        school.replaceCalendar(calendar)
        val lessons = mondayLessons + tuesday
        calendar.addLessons(lessons)
        val day = LocalDate.of(2022, 10, 18)
        day.dayOfWeek shouldBe DayOfWeek.TUESDAY
        shouldThrow<EventCantBeAddedException> {
            val newLesson = CalendarEventFactory.createDateLesson(
                subject = math,
                startTime = LocalTime.of(10, 30),
                endTime = LocalTime.of(12, 30),
                professor = professor,
                studentsClass = studentClass,
                date = day,
            )
            val anotherNewEvent = AlterationFactory.createNewEvent(newLesson)
            calendar.addAlteration(anotherNewEvent)
        }
    }

    "should have an error if adding a event when there is already a new event" {
        val calendar = SchoolCalendar.create(schoolYear)
        school.replaceCalendar(calendar)
        val lessons = mondayLessons + tuesday
        calendar.addLessons(lessons)
        val day = LocalDate.of(2022, 10, 18)
        day.dayOfWeek shouldBe DayOfWeek.TUESDAY
        val newLesson = CalendarEventFactory.createDateLesson(
            subject = math,
            startTime = LocalTime.of(11, 30),
            endTime = LocalTime.of(12, 30),
            professor = professor,
            studentsClass = studentClass,
            date = day,
        )
        val newEvent = AlterationFactory.createNewEvent(newLesson)
        calendar.addAlteration(newEvent)
        shouldThrow<EventCantBeAddedException> {
            val anotherNewEvent = AlterationFactory.createNewEvent(newLesson)
            calendar.addAlteration(anotherNewEvent)
        }
    }
})
