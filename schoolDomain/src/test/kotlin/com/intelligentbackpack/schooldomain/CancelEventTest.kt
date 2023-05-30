package com.intelligentbackpack.schooldomain

import com.intelligentbackpack.schooldomain.entities.Class
import com.intelligentbackpack.schooldomain.entities.School
import com.intelligentbackpack.schooldomain.entities.calendar.CalendarEventFactory
import com.intelligentbackpack.schooldomain.entities.calendar.SchoolCalendar
import com.intelligentbackpack.schooldomain.entities.calendar.alteration.AlterationFactory
import com.intelligentbackpack.schooldomain.entities.person.Professor
import com.intelligentbackpack.schooldomain.exceptions.EventCantBeCancelledException
import com.intelligentbackpack.schooldomain.exceptions.EventDateException
import com.intelligentbackpack.schooldomain.exceptions.EventNotFoundException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class CancelEventTest : StringSpec({

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
    val school = School.create(schoolName, schoolCity)
        .replaceCalendar(calendar)
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

    "should be able to cancel an event on a specific date" {
        val lessons = mondayLessons + tuesday
        var lessonCalendar = calendar.addLessons(lessons)
        val day = LocalDate.of(2022, 10, 10)
        day.dayOfWeek shouldBe DayOfWeek.MONDAY
        val cancelledLesson = AlterationFactory.createCancelSingleDayEvent(day, mondayLesson1)
        lessonCalendar = lessonCalendar.addAlteration(cancelledLesson)
        lessonCalendar.getStudentsEvents(studentClass, day) shouldBe setOf(mondayLesson2)
        lessonCalendar.getProfessorEvents(professor, day) shouldBe setOf(mondayLesson2)
        lessonCalendar.getStudentsEvents(studentClass, day.plusDays(7)) shouldBe mondayLessons
        lessonCalendar.getProfessorEvents(professor, day.plusDays(7)) shouldBe mondayLessons
    }

    "should have an error if the day event to cancel is not in the calendar" {
        val lessons = tuesday + mondayLesson2
        val lessonCalendar = calendar.addLessons(lessons)
        val day = LocalDate.of(2022, 10, 10)
        day.dayOfWeek shouldBe DayOfWeek.MONDAY
        shouldThrow<EventNotFoundException> {
            val cancelledLesson = AlterationFactory.createCancelSingleDayEvent(day, mondayLesson1)
            lessonCalendar.addAlteration(cancelledLesson)
        }
    }

    "should have an error if the event to cancel is not on the same day of the week" {
        val lessons = mondayLessons + tuesday
        val lessonCalendar = calendar.addLessons(lessons)
        val day = LocalDate.of(2022, 10, 11)
        day.dayOfWeek shouldBe DayOfWeek.TUESDAY
        shouldThrow<EventDateException> {
            val anotherCancelledLesson = AlterationFactory.createCancelSingleDayEvent(day, mondayLesson1)
            lessonCalendar.addAlteration(anotherCancelledLesson)
        }
    }

    "should have an error if the event to cancel is not in the calendar on the specified date" {
        val lessons = mondayLessons + tuesday
        val lessonCalendar = calendar.addLessons(lessons)
        val day = LocalDate.of(2022, 8, 8)
        day.dayOfWeek shouldBe DayOfWeek.MONDAY
        shouldThrow<EventDateException> {
            val anotherCancelledLesson = AlterationFactory.createCancelSingleDayEvent(day, mondayLesson2)
            lessonCalendar.addAlteration(anotherCancelledLesson)
        }
    }

    "should have an error if the event to cancel is already canceled" {
        val lessons = mondayLessons + tuesday
        var lessonCalendar = calendar.addLessons(lessons)
        val day = LocalDate.of(2022, 10, 10)
        day.dayOfWeek shouldBe DayOfWeek.MONDAY
        val cancelledLesson = AlterationFactory.createCancelSingleDayEvent(day, mondayLesson1)
        lessonCalendar = lessonCalendar.addAlteration(cancelledLesson)
        shouldThrow<EventCantBeCancelledException> {
            val anotherCancelledLesson = AlterationFactory.createCancelSingleDayEvent(day, mondayLesson1)
            lessonCalendar.addAlteration(anotherCancelledLesson)
        }
    }

    "should be able to cancel an event on an interval" {
        val lessons = mondayLessons + tuesday
        var lessonCalendar = calendar.addLessons(lessons)
        val from = LocalDate.of(2022, 10, 9)
        val to = LocalDate.of(2022, 10, 17)
        val cancelledLesson = AlterationFactory.createCancelIntervalOfDaysEvent(from, to, mondayLesson1)
        lessonCalendar = lessonCalendar.addAlteration(cancelledLesson)
        val firstMondayInInterval = LocalDate.of(2022, 10, 10)
        firstMondayInInterval.dayOfWeek shouldBe DayOfWeek.MONDAY
        val secondMondayInInterval = LocalDate.of(2022, 10, 17)
        secondMondayInInterval.dayOfWeek shouldBe DayOfWeek.MONDAY
        val firstMondayAfterInterval = LocalDate.of(2022, 10, 24)
        firstMondayAfterInterval.dayOfWeek shouldBe DayOfWeek.MONDAY
        lessonCalendar.getStudentsEvents(studentClass, firstMondayInInterval) shouldBe setOf(mondayLesson2)
        lessonCalendar.getProfessorEvents(professor, firstMondayInInterval) shouldBe setOf(mondayLesson2)
        lessonCalendar.getStudentsEvents(studentClass, secondMondayInInterval) shouldBe setOf(mondayLesson2)
        lessonCalendar.getProfessorEvents(professor, secondMondayInInterval) shouldBe setOf(mondayLesson2)
        lessonCalendar.getStudentsEvents(studentClass, firstMondayAfterInterval) shouldBe mondayLessons
        lessonCalendar.getProfessorEvents(professor, firstMondayAfterInterval) shouldBe mondayLessons
    }

    "should have an error if the interval event to cancel is not in the calendar" {
        val lessons = tuesday + mondayLesson2
        val lessonCalendar = calendar.addLessons(lessons)
        val from = LocalDate.of(2022, 10, 9)
        val to = LocalDate.of(2022, 10, 17)
        shouldThrow<EventNotFoundException> {
            val cancelledLesson = AlterationFactory.createCancelIntervalOfDaysEvent(from, to, mondayLesson1)
            lessonCalendar.addAlteration(cancelledLesson)
        }
    }

    "should have an error if the interval event to cancel is not in the interval of the cancelled event" {
        val lessons = mondayLessons + tuesday
        val lessonCalendar = calendar.addLessons(lessons)
        val from = LocalDate.of(2022, 8, 9)
        val to = LocalDate.of(2022, 8, 17)
        shouldThrow<EventDateException> {
            val cancelledLesson = AlterationFactory.createCancelIntervalOfDaysEvent(from, to, mondayLesson1)
            lessonCalendar.addAlteration(cancelledLesson)
        }
    }

    "should have an error if the interval event to cancel is already canceled" {
        val lessons = mondayLessons + tuesday
        var lessonCalendar = calendar.addLessons(lessons)
        val from = LocalDate.of(2022, 10, 9)
        val to = LocalDate.of(2022, 10, 17)
        val cancelledLesson = AlterationFactory.createCancelIntervalOfDaysEvent(from, to, mondayLesson1)
        lessonCalendar = lessonCalendar.addAlteration(cancelledLesson)
        shouldThrow<EventCantBeCancelledException> {
            val anotherCancelledLesson = AlterationFactory.createCancelIntervalOfDaysEvent(from, to, mondayLesson1)
            lessonCalendar.addAlteration(anotherCancelledLesson)
        }
    }
})
