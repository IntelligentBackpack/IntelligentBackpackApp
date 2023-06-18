package com.intelligentbackpack.schooldomain

import com.intelligentbackpack.schooldomain.entities.Class
import com.intelligentbackpack.schooldomain.entities.calendar.CalendarEventFactory
import com.intelligentbackpack.schooldomain.entities.calendar.SchoolCalendar
import com.intelligentbackpack.schooldomain.entities.calendar.alteration.AlterationFactory
import com.intelligentbackpack.schooldomain.entities.person.Professor
import com.intelligentbackpack.schooldomain.exceptions.EventCantBeRescheduleException
import com.intelligentbackpack.schooldomain.exceptions.EventDateException
import com.intelligentbackpack.schooldomain.exceptions.EventNotFoundException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class RescheduleEventTest : StringSpec({

    val schoolYear = "2022-2023"
    val class1A = "1A"
    val email = "test@gmail.com"
    val name = "John"
    val surname = "Doe"
    val math = "Math"
    val physics = "Physics"
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

    "should be able to add a rescheduled week event" {
        val lessons = mondayLessons + tuesday
        var lessonCalendar = calendar.addLessons(lessons)
        val initialCancelledDate = LocalDate.of(2022, 10, 17)
        initialCancelledDate.dayOfWeek shouldBe DayOfWeek.MONDAY
        val finalCancelledDate = LocalDate.of(2022, 10, 24)
        finalCancelledDate.dayOfWeek shouldBe DayOfWeek.MONDAY
        val initialRescheduledDate = LocalDate.of(2022, 10, 18)
        initialRescheduledDate.dayOfWeek shouldBe DayOfWeek.TUESDAY
        val finalRescheduledDate = LocalDate.of(2022, 10, 25)
        finalRescheduledDate.dayOfWeek shouldBe DayOfWeek.TUESDAY
        val newLesson = CalendarEventFactory.createWeekLesson(
            day = DayOfWeek.TUESDAY,
            subject = math,
            startTime = LocalTime.of(11, 30),
            endTime = LocalTime.of(12, 30),
            professor = professor,
            fromDate = initialRescheduledDate,
            toDate = finalRescheduledDate,
            studentsClass = studentClass,
        )
        val newEvent = AlterationFactory.createRescheduleIntervalOfDaysEvent(
            initialCancelledDate,
            finalCancelledDate,
            newEvent = newLesson,
            originalEvent = mondayLesson1,
        )
        lessonCalendar = lessonCalendar.addAlteration(newEvent)
        val tuesdayInInterval = LocalDate.of(2022, 10, 18)
        tuesdayInInterval.dayOfWeek shouldBe DayOfWeek.TUESDAY
        lessonCalendar.getStudentsEvents(
            studentClass,
            tuesdayInInterval,
        ) shouldBe (tuesday + newLesson).sortedBy { it.startTime }
        lessonCalendar.getProfessorEvents(
            professor,
            tuesdayInInterval,
        ) shouldBe (tuesday + newLesson).sortedBy { it.startTime }
        val mondayInInterval = LocalDate.of(2022, 10, 17)
        mondayInInterval.dayOfWeek shouldBe DayOfWeek.MONDAY
        lessonCalendar.getStudentsEvents(studentClass, mondayInInterval) shouldBe listOf(mondayLesson2)
        lessonCalendar.getProfessorEvents(professor, mondayInInterval) shouldBe listOf(mondayLesson2)
    }

    "should have an error if the original event is not in the calendar" {
        val lessons = tuesday + mondayLesson2
        val lessonCalendar = calendar.addLessons(lessons)
        val initialCancelledDate = LocalDate.of(2022, 10, 17)
        initialCancelledDate.dayOfWeek shouldBe DayOfWeek.MONDAY
        val finalCancelledDate = LocalDate.of(2022, 10, 24)
        finalCancelledDate.dayOfWeek shouldBe DayOfWeek.MONDAY
        val initialRescheduledDate = LocalDate.of(2022, 10, 18)
        initialRescheduledDate.dayOfWeek shouldBe DayOfWeek.TUESDAY
        val finalRescheduledDate = LocalDate.of(2022, 10, 25)
        finalRescheduledDate.dayOfWeek shouldBe DayOfWeek.TUESDAY
        val newLesson = CalendarEventFactory.createWeekLesson(
            day = DayOfWeek.TUESDAY,
            subject = math,
            startTime = LocalTime.of(11, 30),
            endTime = LocalTime.of(12, 30),
            professor = professor,
            fromDate = initialRescheduledDate,
            toDate = finalRescheduledDate,
            studentsClass = studentClass,
        )
        val newEvent = AlterationFactory.createRescheduleIntervalOfDaysEvent(
            initialCancelledDate,
            finalCancelledDate,
            newEvent = newLesson,
            originalEvent = mondayLesson1,
        )
        shouldThrow<EventNotFoundException> {
            lessonCalendar.addAlteration(newEvent)
        }
    }

    "should have an error if the original event is already rescheduled" {
        val lessons = mondayLessons + tuesday
        var lessonCalendar = calendar.addLessons(lessons)
        val initialCancelledDate = LocalDate.of(2022, 10, 17)
        initialCancelledDate.dayOfWeek shouldBe DayOfWeek.MONDAY
        val finalCancelledDate = LocalDate.of(2022, 10, 24)
        finalCancelledDate.dayOfWeek shouldBe DayOfWeek.MONDAY
        val initialRescheduledDate = LocalDate.of(2022, 10, 18)
        initialRescheduledDate.dayOfWeek shouldBe DayOfWeek.TUESDAY
        val finalRescheduledDate = LocalDate.of(2022, 10, 25)
        finalRescheduledDate.dayOfWeek shouldBe DayOfWeek.TUESDAY
        val newLesson = CalendarEventFactory.createWeekLesson(
            day = DayOfWeek.TUESDAY,
            subject = math,
            startTime = LocalTime.of(11, 30),
            endTime = LocalTime.of(12, 30),
            professor = professor,
            fromDate = initialRescheduledDate,
            toDate = finalRescheduledDate,
            studentsClass = studentClass,
        )
        val newEvent = AlterationFactory.createRescheduleIntervalOfDaysEvent(
            initialCancelledDate,
            finalCancelledDate,
            newEvent = newLesson,
            originalEvent = mondayLesson1,
        )
        lessonCalendar = lessonCalendar.addAlteration(newEvent)
        val anotherNewEvent = AlterationFactory.createRescheduleIntervalOfDaysEvent(
            initialCancelledDate,
            finalCancelledDate,
            newEvent = newLesson,
            originalEvent = mondayLesson1,
        )
        shouldThrow<EventCantBeRescheduleException> {
            lessonCalendar.addAlteration(anotherNewEvent)
        }
    }

    "should have an error if the new week rescheduled event is overlapping with another event" {
        val lessons = mondayLessons + tuesday
        val lessonCalendar = calendar.addLessons(lessons)
        val initialCancelledDate = LocalDate.of(2022, 10, 17)
        initialCancelledDate.dayOfWeek shouldBe DayOfWeek.MONDAY
        val finalCancelledDate = LocalDate.of(2022, 10, 24)
        finalCancelledDate.dayOfWeek shouldBe DayOfWeek.MONDAY
        val initialRescheduledDate = LocalDate.of(2022, 10, 18)
        initialRescheduledDate.dayOfWeek shouldBe DayOfWeek.TUESDAY
        val finalRescheduledDate = LocalDate.of(2022, 10, 25)
        finalRescheduledDate.dayOfWeek shouldBe DayOfWeek.TUESDAY
        val newLesson = CalendarEventFactory.createWeekLesson(
            day = DayOfWeek.TUESDAY,
            subject = math,
            startTime = LocalTime.of(10, 30),
            endTime = LocalTime.of(12, 30),
            professor = professor,
            fromDate = initialRescheduledDate,
            toDate = finalRescheduledDate,
            studentsClass = studentClass,
        )
        val newEvent = AlterationFactory.createRescheduleIntervalOfDaysEvent(
            initialCancelledDate,
            finalCancelledDate,
            newEvent = newLesson,
            originalEvent = mondayLesson1,
        )
        shouldThrow<EventCantBeRescheduleException> {
            lessonCalendar.addAlteration(newEvent)
        }
    }

    "should be able to add a rescheduled date event" {
        val lessons = mondayLessons + tuesday
        var lessonCalendar = calendar.addLessons(lessons)
        val newEventDate = LocalDate.of(2022, 10, 18)
        newEventDate.dayOfWeek shouldBe DayOfWeek.TUESDAY
        val oldEventDate = LocalDate.of(2022, 10, 17)
        oldEventDate.dayOfWeek shouldBe DayOfWeek.MONDAY
        val newLesson = CalendarEventFactory.createDateLesson(
            subject = math,
            startTime = LocalTime.of(11, 30),
            endTime = LocalTime.of(12, 30),
            professor = professor,
            studentsClass = studentClass,
            date = newEventDate,
        )
        val newEvent = AlterationFactory.createRescheduleSingleDayEvent(
            dateOriginalEvent = oldEventDate,
            newEvent = newLesson,
            originalEvent = mondayLesson1,
        )
        lessonCalendar = lessonCalendar.addAlteration(newEvent)
        lessonCalendar.getStudentsEvents(
            studentClass,
            newEventDate,
        ) shouldBe (tuesday + newLesson).sortedBy { it.startTime }
        lessonCalendar.getProfessorEvents(
            professor,
            newEventDate,
        ) shouldBe (tuesday + newLesson).sortedBy { it.startTime }
        lessonCalendar.getStudentsEvents(studentClass, oldEventDate) shouldBe listOf(mondayLesson2)
        lessonCalendar.getProfessorEvents(professor, oldEventDate) shouldBe listOf(mondayLesson2)
    }

    "should have an error if the date of the event rescheduled is not one of day of the original event" {
        val newEventDate = LocalDate.of(2022, 10, 19)
        newEventDate.dayOfWeek shouldBe DayOfWeek.WEDNESDAY
        val oldEventDate = LocalDate.of(2022, 10, 18)
        oldEventDate.dayOfWeek shouldBe DayOfWeek.TUESDAY
        val newLesson = CalendarEventFactory.createDateLesson(
            subject = math,
            startTime = LocalTime.of(11, 30),
            endTime = LocalTime.of(12, 30),
            professor = professor,
            studentsClass = studentClass,
            date = newEventDate,
        )
        shouldThrow<EventDateException> {
            AlterationFactory.createRescheduleSingleDayEvent(
                dateOriginalEvent = oldEventDate,
                newEvent = newLesson,
                originalEvent = mondayLesson1,
            )
        }
    }

    "should have an error if the day of the event have already an event" {
        val lessons = mondayLessons + tuesday
        var lessonCalendar = calendar.addLessons(lessons)
        val newEventDate = LocalDate.of(2022, 10, 18)
        newEventDate.dayOfWeek shouldBe DayOfWeek.TUESDAY
        val oldEventDate = LocalDate.of(2022, 10, 17)
        oldEventDate.dayOfWeek shouldBe DayOfWeek.MONDAY
        val newLesson = CalendarEventFactory.createDateLesson(
            subject = math,
            startTime = LocalTime.of(11, 30),
            endTime = LocalTime.of(12, 30),
            professor = professor,
            studentsClass = studentClass,
            date = newEventDate,
        )
        val newEvent = AlterationFactory.createRescheduleSingleDayEvent(
            dateOriginalEvent = oldEventDate,
            newEvent = newLesson,
            originalEvent = mondayLesson1,
        )
        lessonCalendar = lessonCalendar.addAlteration(newEvent)
        val anotherNewLesson = CalendarEventFactory.createDateLesson(
            subject = math,
            startTime = LocalTime.of(10, 30),
            endTime = LocalTime.of(12, 30),
            professor = professor,
            studentsClass = studentClass,
            date = newEventDate,
        )
        val anotherNewEvent = AlterationFactory.createRescheduleSingleDayEvent(
            dateOriginalEvent = oldEventDate,
            newEvent = anotherNewLesson,
            originalEvent = mondayLesson1,
        )
        shouldThrow<EventCantBeRescheduleException> {
            lessonCalendar.addAlteration(anotherNewEvent)
        }
    }
})
