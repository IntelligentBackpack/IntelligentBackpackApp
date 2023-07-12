package com.intelligentbackpack.reminderdomain

import com.intelligentbackpack.reminderdomain.adapter.EventAdapter
import com.intelligentbackpack.reminderdomain.entitites.Reminder
import com.intelligentbackpack.reminderdomain.entitites.ReminderForLessonDate
import com.intelligentbackpack.reminderdomain.entitites.ReminderForLessonIntervalPeriod
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class ReminderTest : StringSpec({
    val subject = "math"
    val isbn = "9788843025343"
    val reminder = Reminder.create()
    val lesson = EventAdapter.WeekLessonImpl(
        startTime = LocalTime.of(10, 0),
        endTime = LocalTime.of(11, 0),
        dayOfWeek = DayOfWeek.MONDAY,
        fromDate = LocalDate.of(2021, 1, 1),
        toDate = LocalDate.of(2021, 1, 31),
        subject = subject,
    )

    "should be able to create a reminder for a lesson date" {
        val reminderForLesson = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 1),
        )

        reminderForLesson.isInInterval(LocalDate.of(2021, 1, 1)) shouldBe true
        reminderForLesson.isInInterval(LocalDate.of(2021, 1, 2)) shouldBe false
    }

    "should be able to create a reminder for a lesson week" {
        val reminderForLesson = ReminderForLessonIntervalPeriod.create(
            isbn = isbn,
            lesson = lesson,
            startDate = LocalDate.of(2021, 1, 1),
            endDate = LocalDate.of(2021, 1, 31),
        )
        reminderForLesson.isInInterval(LocalDate.of(2021, 1, 1)) shouldBe true
        reminderForLesson.isInInterval(LocalDate.of(2021, 1, 31)) shouldBe true
        reminderForLesson.isInInterval(LocalDate.of(2021, 2, 1)) shouldBe false
    }

    "should throw an exception when creating a reminder for a lesson date with a wrong date" {
        shouldThrow<IllegalArgumentException> {
            ReminderForLessonDate.create(
                isbn = isbn,
                lesson = EventAdapter.DateLessonImpl(
                    startTime = LocalTime.of(10, 0),
                    endTime = LocalTime.of(11, 0),
                    subject = subject,
                    date = LocalDate.of(2021, 2, 1),
                ),
                date = LocalDate.of(2021, 1, 1),
            )
        }
    }

    "should throw an exception when creating a reminder for a lesson week with a wrong date" {
        shouldThrow<IllegalArgumentException> {
            ReminderForLessonIntervalPeriod.create(
                isbn = isbn,
                lesson = lesson,
                startDate = LocalDate.of(2021, 1, 1),
                endDate = LocalDate.of(2021, 2, 1),
            )
        }
    }

    "should be able to add a book to a lesson" {
        val reminderForLesson = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 1),
        )
        val newReminder = reminder.addBookForLesson(reminderForLesson)
        newReminder.getLessonsForBook(isbn) shouldBe setOf(reminderForLesson)
        newReminder.getBooksForLesson(lesson) shouldBe setOf(reminderForLesson)
    }

    "should be able to have more than one element" {
        val initialReminders = setOf(
            ReminderForLessonDate.create(
                isbn = isbn,
                lesson = lesson,
                date = LocalDate.of(2021, 1, 1),
            ),
        )
        val oneElementReminder = Reminder.create(initialReminders)
        val newReminderForLesson = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 2),
        )
        val newReminder = oneElementReminder.addBookForLesson(newReminderForLesson)
        newReminder.getLessonsForBook(isbn) shouldBe initialReminders + newReminderForLesson
        newReminder.getBooksForLesson(lesson) shouldBe initialReminders + newReminderForLesson
        newReminder.getBooksForLessonInDate(lesson, LocalDate.of(2021, 1, 1)) shouldBe setOf(isbn)
        newReminder.getBooksForLessonInDate(lesson, LocalDate.of(2021, 1, 2)) shouldBe setOf(isbn)
        newReminder.getBooksForLessonInDate(lesson, LocalDate.of(2021, 1, 3)) shouldBe setOf()
    }

    "should be able to have more than one book" {
        val newIsbn = "9788843025344"
        val initialReminders = setOf(
            ReminderForLessonDate.create(
                isbn = isbn,
                lesson = lesson,
                date = LocalDate.of(2021, 1, 1),
            ),
        )
        val oneElementReminder = Reminder.create(initialReminders)
        val newReminderForLesson = ReminderForLessonDate.create(
            isbn = newIsbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 1),
        )
        val newReminder = oneElementReminder.addBookForLesson(newReminderForLesson)
        newReminder.getLessonsForBook(isbn) shouldBe initialReminders
        newReminder.getLessonsForBook(newIsbn) shouldBe setOf(newReminderForLesson)
        newReminder.getBooksForLesson(lesson) shouldBe initialReminders + newReminderForLesson
        newReminder.getBooksForLessonInDate(lesson, LocalDate.of(2021, 1, 1)) shouldBe setOf(isbn, newIsbn)
        newReminder.getBooksForLessonInDate(lesson, LocalDate.of(2021, 1, 2)) shouldBe setOf()
    }

    "should be able to remove a book from a lesson" {
        val reminderForLesson = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 1),
        )
        val newReminder = reminder.addBookForLesson(reminderForLesson)
        newReminder.getLessonsForBook(isbn) shouldBe setOf(reminderForLesson)
        newReminder.getBooksForLesson(lesson) shouldBe setOf(reminderForLesson)
        val reminderWithoutBook = newReminder.removeBookForLesson(reminderForLesson)
        reminderWithoutBook.getLessonsForBook(isbn) shouldBe setOf()
        reminderWithoutBook.getBooksForLesson(lesson) shouldBe setOf()
    }

    "should have a error when removing a book from a lesson that is not in the reminder of the book" {
        val reminderForLesson = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 1),
        )
        val newReminder = reminder.addBookForLesson(reminderForLesson)
        newReminder.getLessonsForBook(isbn) shouldBe setOf(reminderForLesson)
        newReminder.getBooksForLesson(lesson) shouldBe setOf(reminderForLesson)
        shouldThrow<IllegalArgumentException> {
            newReminder.removeBookForLesson(
                ReminderForLessonDate.create(
                    isbn = "9788843025344",
                    lesson = lesson,
                    date = LocalDate.of(2021, 1, 1),
                ),
            )
        }
    }

    "should have a error when removing a book from a lesson that is not in the reminder of the lesson" {
        val reminderForLesson = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 1),
        )
        val newReminder = reminder.addBookForLesson(reminderForLesson)
        newReminder.getLessonsForBook(isbn) shouldBe setOf(reminderForLesson)
        newReminder.getBooksForLesson(lesson) shouldBe setOf(reminderForLesson)
        shouldThrow<IllegalArgumentException> {
            newReminder.removeBookForLesson(
                ReminderForLessonDate.create(
                    isbn = isbn,
                    lesson = EventAdapter.DateLessonImpl(
                        startTime = LocalTime.of(10, 0),
                        endTime = LocalTime.of(11, 0),
                        subject = subject,
                        date = LocalDate.of(2021, 1, 2),
                    ),
                    date = LocalDate.of(2021, 1, 1),
                ),
            )
        }
    }

    "should be able to change the book for a lesson" {
        val reminderForLesson = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 1),
        )
        val newReminder = reminder.addBookForLesson(reminderForLesson)
        newReminder.getLessonsForBook(isbn) shouldBe setOf(reminderForLesson)
        newReminder.getBooksForLesson(lesson) shouldBe setOf(reminderForLesson)
        val reminderForLessonUpdated = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 2),
        )
        val reminderUpdated =
            newReminder.changePeriodOfBookForLesson(reminderForLesson, reminderForLessonUpdated)
        reminderUpdated.getLessonsForBook(isbn) shouldBe setOf(reminderForLessonUpdated)
        reminderUpdated.getBooksForLessonInDate(lesson, LocalDate.of(2021, 1, 2)) shouldBe setOf(isbn)
        reminderUpdated.getBooksForLessonInDate(lesson, LocalDate.of(2021, 1, 1)) shouldBe setOf()
    }

    "should have an error if the old reminder is not in the reminder" {
        val reminderForLesson = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 1),
        )
        val newReminder = reminder.addBookForLesson(reminderForLesson)
        newReminder.getLessonsForBook(isbn) shouldBe setOf(reminderForLesson)
        newReminder.getBooksForLesson(lesson) shouldBe setOf(reminderForLesson)
        val reminderForLessonUpdated = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 2),
        )
        shouldThrow<IllegalArgumentException> {
            newReminder.changePeriodOfBookForLesson(
                ReminderForLessonDate.create(
                    isbn = isbn,
                    lesson = lesson,
                    date = LocalDate.of(2021, 1, 3),
                ),
                reminderForLessonUpdated,
            )
        }
    }

    "should have an error if the new reminder has a different lesson" {
        val reminderForLesson = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 1),
        )
        val newReminder = reminder.addBookForLesson(reminderForLesson)
        newReminder.getLessonsForBook(isbn) shouldBe setOf(reminderForLesson)
        newReminder.getBooksForLesson(lesson) shouldBe setOf(reminderForLesson)
        val reminderForLessonUpdated = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = EventAdapter.DateLessonImpl(
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                subject = subject,
                date = LocalDate.of(2021, 1, 2),
            ),
            date = LocalDate.of(2021, 1, 2),
        )
        shouldThrow<IllegalArgumentException> {
            newReminder.changePeriodOfBookForLesson(reminderForLesson, reminderForLessonUpdated)
        }
    }

    "should have an error if the new reminder has a different book" {
        val reminderForLesson = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 1),
        )
        val newReminder = reminder.addBookForLesson(reminderForLesson)
        newReminder.getLessonsForBook(isbn) shouldBe setOf(reminderForLesson)
        newReminder.getBooksForLesson(lesson) shouldBe setOf(reminderForLesson)
        val reminderForLessonUpdated = ReminderForLessonDate.create(
            isbn = "9788843025344",
            lesson = lesson,
            date = LocalDate.of(2021, 1, 2),
        )
        shouldThrow<IllegalArgumentException> {
            newReminder.changePeriodOfBookForLesson(reminderForLesson, reminderForLessonUpdated)
        }
    }
})
