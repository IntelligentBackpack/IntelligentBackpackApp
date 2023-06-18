package com.intelligentbackpack.schooldomain

import com.intelligentbackpack.schooldomain.entities.Class
import com.intelligentbackpack.schooldomain.entities.calendar.CalendarEventFactory
import com.intelligentbackpack.schooldomain.entities.calendar.SchoolCalendar
import com.intelligentbackpack.schooldomain.entities.calendar.alteration.AlterationFactory
import com.intelligentbackpack.schooldomain.entities.person.Professor
import com.intelligentbackpack.schooldomain.exceptions.EventCantBeAddedException
import com.intelligentbackpack.schooldomain.exceptions.EventCantBeCancelledException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class AlterationTest : StringSpec({

    val schoolYear = "2022-2023"
    val class1A = "1A"
    val email = "test@gmail.com"
    val name = "John"
    val surname = "Doe"
    val math = "Math"
    val physics = "Physics"
    val history = "History"
    val calendar = SchoolCalendar.create(schoolYear)
    val studentClass = Class.create(class1A)
    val professor = Professor.create(email, name, surname)
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

    "should be able to create a new lesson where there is one cancelled" {
        val lessons = mondayLessons + tuesday
        var lessonCalendar = calendar.addLessons(lessons)
        val day = LocalDate.of(2022, 10, 10)
        day.dayOfWeek shouldBe DayOfWeek.MONDAY
        val cancelledLesson = AlterationFactory.createCancelSingleDayEvent(day, mondayLesson1)
        lessonCalendar = lessonCalendar.addAlteration(cancelledLesson)
        val newLesson = CalendarEventFactory.createDateLesson(
            subject = history,
            startTime = LocalTime.of(8, 30),
            endTime = LocalTime.of(9, 30),
            professor = professor,
            studentsClass = studentClass,
            date = day,
        )
        val newEvent = AlterationFactory.createNewEvent(newLesson)
        lessonCalendar = lessonCalendar.addAlteration(newEvent)
        lessonCalendar.getStudentsEvents(studentClass, day) shouldBe listOf(newLesson, mondayLesson2)
        lessonCalendar.getProfessorEvents(professor, day) shouldBe listOf(newLesson, mondayLesson2)
    }

    "should be able to create a new lesson where there is one rescheduled" {
        val lessons = mondayLessons + tuesday
        var lessonCalendar = calendar.addLessons(lessons)
        val dayOriginal = LocalDate.of(2022, 10, 10)
        dayOriginal.dayOfWeek shouldBe DayOfWeek.MONDAY
        val dayNewEvent = LocalDate.of(2022, 10, 11)
        dayNewEvent.dayOfWeek shouldBe DayOfWeek.TUESDAY
        val newLesson1 = CalendarEventFactory.createDateLesson(
            subject = history,
            startTime = LocalTime.of(8, 30),
            endTime = LocalTime.of(9, 30),
            professor = professor,
            studentsClass = studentClass,
            date = dayNewEvent,
        )
        val rescheduledLesson = AlterationFactory.createRescheduleSingleDayEvent(newLesson1, dayOriginal, mondayLesson1)
        lessonCalendar = lessonCalendar.addAlteration(rescheduledLesson)
        val newLesson2 = CalendarEventFactory.createDateLesson(
            subject = history,
            startTime = LocalTime.of(8, 30),
            endTime = LocalTime.of(9, 30),
            professor = professor,
            studentsClass = studentClass,
            date = dayOriginal,
        )
        val newEvent = AlterationFactory.createNewEvent(newLesson2)
        lessonCalendar = lessonCalendar.addAlteration(newEvent)
        lessonCalendar.getStudentsEvents(studentClass, dayOriginal) shouldBe listOf(newLesson2, mondayLesson2)
        lessonCalendar.getProfessorEvents(professor, dayOriginal) shouldBe listOf(newLesson2, mondayLesson2)
    }

    "should have an error when cancel a event that is rescheduled" {
        val lessons = mondayLessons + tuesday
        var lessonCalendar = calendar.addLessons(lessons)
        val dayOriginal = LocalDate.of(2022, 10, 10)
        dayOriginal.dayOfWeek shouldBe DayOfWeek.MONDAY
        val dayNewEvent = LocalDate.of(2022, 10, 11)
        dayNewEvent.dayOfWeek shouldBe DayOfWeek.TUESDAY
        val newLesson1 = CalendarEventFactory.createDateLesson(
            subject = history,
            startTime = LocalTime.of(8, 30),
            endTime = LocalTime.of(9, 30),
            professor = professor,
            studentsClass = studentClass,
            date = dayNewEvent,
        )
        val rescheduledLesson = AlterationFactory.createRescheduleSingleDayEvent(newLesson1, dayOriginal, mondayLesson1)
        lessonCalendar = lessonCalendar.addAlteration(rescheduledLesson)
        shouldThrow<EventCantBeCancelledException> {
            val cancelledLesson = AlterationFactory.createCancelSingleDayEvent(dayOriginal, mondayLesson1)
            lessonCalendar.addAlteration(cancelledLesson)
        }
    }

    "should be able to delete and reschedule the same event in different dates" {
        val lessons = mondayLessons + tuesday
        var lessonCalendar = calendar.addLessons(lessons)
        val dayOriginal = LocalDate.of(2022, 10, 10)
        dayOriginal.dayOfWeek shouldBe DayOfWeek.MONDAY
        val dayNewEvent = LocalDate.of(2022, 10, 11)
        dayNewEvent.dayOfWeek shouldBe DayOfWeek.TUESDAY
        val newLesson1 = CalendarEventFactory.createDateLesson(
            subject = history,
            startTime = LocalTime.of(8, 30),
            endTime = LocalTime.of(9, 30),
            professor = professor,
            studentsClass = studentClass,
            date = dayNewEvent,
        )
        val rescheduledLesson = AlterationFactory.createRescheduleSingleDayEvent(newLesson1, dayOriginal, mondayLesson1)
        lessonCalendar = lessonCalendar.addAlteration(rescheduledLesson)
        val deleteDate = LocalDate.of(2022, 10, 17)
        val cancelledLesson = AlterationFactory.createCancelSingleDayEvent(deleteDate, mondayLesson1)
        lessonCalendar = lessonCalendar.addAlteration(cancelledLesson)
        lessonCalendar.getStudentsEvents(studentClass, dayOriginal) shouldBe listOf(mondayLesson2)
        lessonCalendar.getStudentsEvents(studentClass, deleteDate) shouldBe listOf(mondayLesson2)
    }

    "should have an error when create an event where there is a rescheduled one" {
        val lessons = mondayLessons + tuesday
        var lessonCalendar = calendar.addLessons(lessons)
        val dayOriginal = LocalDate.of(2022, 10, 10)
        dayOriginal.dayOfWeek shouldBe DayOfWeek.MONDAY
        val dayNewEvent = LocalDate.of(2022, 10, 11)
        dayNewEvent.dayOfWeek shouldBe DayOfWeek.TUESDAY
        val newLesson1 = CalendarEventFactory.createDateLesson(
            subject = history,
            startTime = LocalTime.of(8, 30),
            endTime = LocalTime.of(9, 30),
            professor = professor,
            studentsClass = studentClass,
            date = dayNewEvent,
        )
        val rescheduledLesson = AlterationFactory.createRescheduleSingleDayEvent(newLesson1, dayOriginal, mondayLesson1)
        lessonCalendar = lessonCalendar.addAlteration(rescheduledLesson)
        val newLesson2 = CalendarEventFactory.createDateLesson(
            subject = history,
            startTime = LocalTime.of(8, 30),
            endTime = LocalTime.of(9, 30),
            professor = professor,
            studentsClass = studentClass,
            date = dayNewEvent,
        )
        shouldThrow<EventCantBeAddedException> {
            val newEvent = AlterationFactory.createNewEvent(newLesson2)
            lessonCalendar.addAlteration(newEvent)
        }
    }

    "should have an error when reschedule an event where there is a new one" {
        val lessons = mondayLessons + tuesday
        var lessonCalendar = calendar.addLessons(lessons)
        val dayOriginal = LocalDate.of(2022, 10, 10)
        dayOriginal.dayOfWeek shouldBe DayOfWeek.MONDAY
        val dayNewEvent = LocalDate.of(2022, 10, 11)
        dayNewEvent.dayOfWeek shouldBe DayOfWeek.TUESDAY
        val newLesson1 = CalendarEventFactory.createDateLesson(
            subject = history,
            startTime = LocalTime.of(8, 30),
            endTime = LocalTime.of(9, 30),
            professor = professor,
            studentsClass = studentClass,
            date = dayNewEvent,
        )
        val newLesson2 = CalendarEventFactory.createDateLesson(
            subject = history,
            startTime = LocalTime.of(8, 30),
            endTime = LocalTime.of(9, 30),
            professor = professor,
            studentsClass = studentClass,
            date = dayNewEvent,
        )
        val rescheduledLesson = AlterationFactory.createRescheduleSingleDayEvent(newLesson2, dayOriginal, mondayLesson1)
        lessonCalendar = lessonCalendar.addAlteration(rescheduledLesson)
        shouldThrow<EventCantBeAddedException> {
            val newEvent = AlterationFactory.createNewEvent(newLesson1)
            lessonCalendar.addAlteration(newEvent)
        }
    }

    "should be able to get all the event for a student" {
        val lessons = mondayLessons + tuesday
        var lessonCalendar = calendar.addLessons(lessons)
        val dayOriginal = LocalDate.of(2022, 10, 10)
        dayOriginal.dayOfWeek shouldBe DayOfWeek.MONDAY
        val dayNewEvent = LocalDate.of(2022, 10, 11)
        dayNewEvent.dayOfWeek shouldBe DayOfWeek.TUESDAY
        val newLesson1 = CalendarEventFactory.createDateLesson(
            subject = history,
            startTime = LocalTime.of(8, 30),
            endTime = LocalTime.of(9, 30),
            professor = professor,
            studentsClass = studentClass,
            date = dayNewEvent,
        )
        val newLesson2 = CalendarEventFactory.createDateLesson(
            subject = math,
            startTime = LocalTime.of(9, 30),
            endTime = LocalTime.of(10, 30),
            professor = professor,
            studentsClass = studentClass,
            date = dayNewEvent,
        )
        val rescheduledLesson = AlterationFactory.createRescheduleSingleDayEvent(newLesson2, dayOriginal, mondayLesson1)
        lessonCalendar = lessonCalendar.addAlteration(rescheduledLesson)
        val newEvent = AlterationFactory.createNewEvent(newLesson1)
        lessonCalendar = lessonCalendar.addAlteration(newEvent)
        lessonCalendar.getAllStudentEvents(studentClass) shouldBe setOf(
            newLesson1,
            newLesson2,
        ) + mondayLessons + tuesday
    }

    "should be able to get all the event for a professor" {
        val lessons = mondayLessons + tuesday
        var lessonCalendar = calendar.addLessons(lessons)
        val dayOriginal = LocalDate.of(2022, 10, 10)
        dayOriginal.dayOfWeek shouldBe DayOfWeek.MONDAY
        val dayNewEvent = LocalDate.of(2022, 10, 11)
        dayNewEvent.dayOfWeek shouldBe DayOfWeek.TUESDAY
        val newLesson1 = CalendarEventFactory.createDateLesson(
            subject = history,
            startTime = LocalTime.of(8, 30),
            endTime = LocalTime.of(9, 30),
            professor = professor,
            studentsClass = studentClass,
            date = dayNewEvent,
        )
        val newLesson2 = CalendarEventFactory.createDateLesson(
            subject = math,
            startTime = LocalTime.of(9, 30),
            endTime = LocalTime.of(10, 30),
            professor = professor,
            studentsClass = studentClass,
            date = dayNewEvent,
        )
        val rescheduledLesson = AlterationFactory.createRescheduleSingleDayEvent(newLesson2, dayOriginal, mondayLesson1)
        lessonCalendar = lessonCalendar.addAlteration(rescheduledLesson)
        val newEvent = AlterationFactory.createNewEvent(newLesson1)
        lessonCalendar = lessonCalendar.addAlteration(newEvent)
        lessonCalendar.getAllProfessorEvents(professor) shouldBe setOf(newLesson1, newLesson2) + mondayLessons + tuesday
    }
})
