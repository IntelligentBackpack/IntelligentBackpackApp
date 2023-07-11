package com.intelligentbackpack.reminderdomain

import com.intelligentbackpack.accessdomain.entities.Role
import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.accessdomain.exceptions.ActionNotAllowedForUserException
import com.intelligentbackpack.accessdomain.usecase.AccessUseCase
import com.intelligentbackpack.desktopdomain.entities.Book
import com.intelligentbackpack.desktopdomain.entities.BookCopy
import com.intelligentbackpack.desktopdomain.entities.Desktop
import com.intelligentbackpack.desktopdomain.usecase.DesktopUseCase
import com.intelligentbackpack.reminderdomain.adapter.EventAdapter
import com.intelligentbackpack.reminderdomain.adapter.ReminderWithSupply
import com.intelligentbackpack.reminderdomain.entitites.Reminder
import com.intelligentbackpack.reminderdomain.entitites.ReminderForLessonDate
import com.intelligentbackpack.reminderdomain.repository.ReminderDomainRepository
import com.intelligentbackpack.reminderdomain.usecase.ReminderUseCase
import com.intelligentbackpack.schooldomain.entities.Class
import com.intelligentbackpack.schooldomain.entities.calendar.CalendarEventFactory
import com.intelligentbackpack.schooldomain.entities.person.Professor
import com.intelligentbackpack.schooldomain.usecase.SchoolUseCase
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class UseCaseTest : StringSpec({

    val repository = mockk<ReminderDomainRepository>(relaxed = true)
    val accessUseCase = mockk<AccessUseCase>(relaxed = true)
    val desktopUseCase = mockk<DesktopUseCase>(relaxed = true)
    val schoolUseCase = mockk<SchoolUseCase>(relaxed = true)

    "should be able to download if the user is a student" {
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(studentUser)
        val useCase = ReminderUseCase(accessUseCase, desktopUseCase, schoolUseCase, repository)
        val result = useCase.downloadReminder()
        result.isSuccess shouldBe true
        coVerify { repository.downloadReminder(any()) }
    }

    "should be able to download if the user is a professor" {
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(professorUser)
        val useCase = ReminderUseCase(accessUseCase, desktopUseCase, schoolUseCase, repository)
        val result = useCase.downloadReminder()
        result.isSuccess shouldBe true
        coVerify { repository.downloadReminder(any()) }
    }

    "should have an error if the user is not a professor or a student" {
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(notAllowedUser)
        val useCase = ReminderUseCase(accessUseCase, desktopUseCase, schoolUseCase, repository)
        val result = useCase.downloadReminder()
        result.isFailure shouldBe true
        result.exceptionOrNull() shouldBe ActionNotAllowedForUserException()
    }

    "should be able to add a school supply if user is a professor" {
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(professorUser)
        val useCase = ReminderUseCase(accessUseCase, desktopUseCase, schoolUseCase, repository)
        val reminderForLesson = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 1),
        )
        coEvery { repository.addBookForLesson(any(), any()) } answers { }
        val result = useCase.addSchoolSupplyForEvent(reminderForLesson)
        result.isSuccess shouldBe true
        coVerify(exactly = 1) { repository.addBookForLesson(reminderForLesson, professorUser) }
    }

    "should have an error if the user is a student and try to add a school supply" {
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(studentUser)
        val useCase = ReminderUseCase(accessUseCase, desktopUseCase, schoolUseCase, repository)
        val reminderForLesson = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 1),
        )
        val result = useCase.addSchoolSupplyForEvent(reminderForLesson)
        result.isFailure shouldBe true
        result.exceptionOrNull() shouldBe ActionNotAllowedForUserException()
    }

    "should have an error if the user is not a professor or a student and try to add a school supply" {
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(notAllowedUser)
        val useCase = ReminderUseCase(accessUseCase, desktopUseCase, schoolUseCase, repository)
        val reminderForLesson = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 1),
        )
        val result = useCase.addSchoolSupplyForEvent(reminderForLesson)
        result.isFailure shouldBe true
        result.exceptionOrNull() shouldBe ActionNotAllowedForUserException()
    }

    "should be able to remove a school supply if user is a professor" {
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(professorUser)
        val useCase = ReminderUseCase(accessUseCase, desktopUseCase, schoolUseCase, repository)
        val reminderForLesson = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 1),
        )
        val reminder = Reminder.create(setOf(reminderForLesson))
        coEvery { repository.getReminder() } returns reminder
        coEvery { repository.removeBookForLesson(any(), any()) } answers { }
        val result = useCase.removeSchoolSupplyForEvent(reminderForLesson)
        result.isSuccess shouldBe true
        coVerify(exactly = 1) { repository.removeBookForLesson(reminderForLesson, professorUser) }
    }

    "should have an error if the user is a student and try to remove a school supply" {
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(studentUser)
        val useCase = ReminderUseCase(accessUseCase, desktopUseCase, schoolUseCase, repository)
        val reminderForLesson = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 1),
        )
        val result = useCase.removeSchoolSupplyForEvent(reminderForLesson)
        result.isFailure shouldBe true
        result.exceptionOrNull() shouldBe ActionNotAllowedForUserException()
    }

    "should have an error if the user is not a professor or a student and try to remove a school supply" {
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(notAllowedUser)
        val useCase = ReminderUseCase(accessUseCase, desktopUseCase, schoolUseCase, repository)
        val reminderForLesson = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 1),
        )
        val result = useCase.removeSchoolSupplyForEvent(reminderForLesson)
        result.isFailure shouldBe true
        result.exceptionOrNull() shouldBe ActionNotAllowedForUserException()
    }

    "should be able to change a reminder if the user is a professor" {
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(professorUser)
        val useCase = ReminderUseCase(accessUseCase, desktopUseCase, schoolUseCase, repository)
        val oldReminderForLesson = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 1),
        )
        val newReminderForLesson = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 2),
        )
        val reminder = Reminder.create(setOf(oldReminderForLesson))
        coEvery { repository.getReminder() } returns reminder
        coEvery { repository.changeBookForLesson(any(), any(), any()) } answers { }
        val result = useCase.changeSchoolSupplyForEvent(oldReminderForLesson, newReminderForLesson)
        result.isSuccess shouldBe true
        coVerify(exactly = 1) {
            repository.changeBookForLesson(
                oldReminderForLesson,
                newReminderForLesson,
                professorUser,
            )
        }
    }

    "should have an error if the user is a student and try to change a reminder" {
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(studentUser)
        val useCase = ReminderUseCase(accessUseCase, desktopUseCase, schoolUseCase, repository)
        val oldReminderForLesson = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 1),
        )
        val newReminderForLesson = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 2),
        )
        val result = useCase.changeSchoolSupplyForEvent(oldReminderForLesson, newReminderForLesson)
        result.isFailure shouldBe true
        result.exceptionOrNull() shouldBe ActionNotAllowedForUserException()
    }

    "should have an error if the user is not a professor or a student and try to change a reminder" {
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(notAllowedUser)
        val useCase = ReminderUseCase(accessUseCase, desktopUseCase, schoolUseCase, repository)
        val oldReminderForLesson = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 1),
        )
        val newReminderForLesson = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 2),
        )
        val result = useCase.changeSchoolSupplyForEvent(oldReminderForLesson, newReminderForLesson)
        result.isFailure shouldBe true
        result.exceptionOrNull() shouldBe ActionNotAllowedForUserException()
    }

    "should have an error if the user is a professor and try to change a reminder that does not exist" {
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(professorUser)
        val useCase = ReminderUseCase(accessUseCase, desktopUseCase, schoolUseCase, repository)
        val oldReminderForLesson = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 1),
        )
        val newReminderForLesson = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 2),
        )
        coEvery { repository.getReminder() } returns Reminder.create()
        val result = useCase.changeSchoolSupplyForEvent(oldReminderForLesson, newReminderForLesson)
        result.isFailure shouldBe true
        (result.exceptionOrNull() is IllegalArgumentException) shouldBe true
    }

    "should be able to get the book for a lesson if the user is a professor" {
        val lessonCalendar = CalendarEventFactory.createWeekLesson(
            subject = lesson.subject,
            startTime = lesson.startTime,
            endTime = lesson.endTime,
            professor = professor,
            studentsClass = studentClass,
            fromDate = lesson.fromDate,
            toDate = lesson.toDate,
            day = lesson.dayOfWeek,
        )
        val reminderForLesson = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 1),
        )
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(professorUser)
        coEvery { desktopUseCase.getBookCopy(any()) } returns Result.success(copy)
        val useCase = ReminderUseCase(accessUseCase, desktopUseCase, schoolUseCase, repository)
        val reminder = Reminder.create(setOf(reminderForLesson))
        coEvery { repository.getReminder() } returns reminder
        val result = useCase.getSchoolSuppliesForEvent(lessonCalendar)
        result.isSuccess shouldBe true
        result.getOrNull() shouldBe setOf(ReminderWithSupply(copy, reminderForLesson))
    }

    "should be able to get the book for a lesson if the user is a student" {
        val lessonCalendar = CalendarEventFactory.createWeekLesson(
            subject = lesson.subject,
            startTime = lesson.startTime,
            endTime = lesson.endTime,
            professor = professor,
            studentsClass = studentClass,
            fromDate = lesson.fromDate,
            toDate = lesson.toDate,
            day = lesson.dayOfWeek,
        )
        val reminderForLesson = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 1),
        )
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(studentUser)
        coEvery { desktopUseCase.getBookCopy(any()) } returns Result.success(copy)
        val useCase = ReminderUseCase(accessUseCase, desktopUseCase, schoolUseCase, repository)
        val reminder = Reminder.create(setOf(reminderForLesson))
        coEvery { repository.getReminder() } returns reminder
        val result = useCase.getSchoolSuppliesForEvent(lessonCalendar)
        result.isSuccess shouldBe true
        result.getOrNull() shouldBe setOf(ReminderWithSupply(copy, reminderForLesson))
    }

    "should have an error if the user isn't a student or a professor and try to get the book for a lesson" {
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(notAllowedUser)
        val useCase = ReminderUseCase(accessUseCase, desktopUseCase, schoolUseCase, repository)
        val result = useCase.getSchoolSuppliesForEvent(lessonCalendar)
        result.isFailure shouldBe true
        result.exceptionOrNull() shouldBe ActionNotAllowedForUserException()
    }

    "should be able to get the event for a book if the user is a professor" {
        val reminderForBook = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 1),
        )
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(professorUser)
        val useCase = ReminderUseCase(accessUseCase, desktopUseCase, schoolUseCase, repository)
        val reminder = Reminder.create(setOf(reminderForBook))
        coEvery { repository.getReminder() } returns reminder
        coEvery { schoolUseCase.userAllSchoolEvents() } returns Result.success(setOf(lessonCalendar))
        val result = useCase.getEventsForSchoolSupply(copy)
        result.isSuccess shouldBe true
        result.getOrNull() shouldBe setOf(lessonCalendar)
    }

    "should be able to get the event for a book if the user is a student" {
        val reminderForBook = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = LocalDate.of(2021, 1, 1),
        )
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(studentUser)
        val useCase = ReminderUseCase(accessUseCase, desktopUseCase, schoolUseCase, repository)
        val reminder = Reminder.create(setOf(reminderForBook))
        coEvery { repository.getReminder() } returns reminder
        coEvery { schoolUseCase.userAllSchoolEvents() } returns Result.success(setOf(lessonCalendar))
        val result = useCase.getEventsForSchoolSupply(copy)
        result.isSuccess shouldBe true
        result.getOrNull() shouldBe setOf(lessonCalendar)
    }

    "should have an error if the user isn't a student or a professor and try to get the event for a book" {
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(notAllowedUser)
        val useCase = ReminderUseCase(accessUseCase, desktopUseCase, schoolUseCase, repository)
        val result = useCase.getEventsForSchoolSupply(copy)
        result.isFailure shouldBe true
        result.exceptionOrNull() shouldBe ActionNotAllowedForUserException()
    }

    "should be able to get the missing school supplies for a date if the user is a professor " +
        "(a missing school supply)" {
            val date = LocalDate.of(2021, 1, 1)
            val reminderForLesson = ReminderForLessonDate.create(
                isbn = isbn,
                lesson = lesson,
                date = date,
            )
            val desktop = Desktop.create(
                schoolSupplies = setOf(copy),
                backpack = "backpack",
            )
            coEvery { accessUseCase.getLoggedUser() } returns Result.success(professorUser)
            coEvery { desktopUseCase.getDesktop() } returns Result.success(desktop)
            coEvery { desktopUseCase.getBookCopy(any()) } returns Result.success(copy)
            coEvery { schoolUseCase.getUserCalendarEventsForDate(date) } returns Result.success(
                listOf(
                    lessonCalendar,
                ),
            )
            val useCase = ReminderUseCase(accessUseCase, desktopUseCase, schoolUseCase, repository)
            val reminder = Reminder.create(setOf(reminderForLesson))
            coEvery { repository.getReminder() } returns reminder
            val result = useCase.remindUserOfMissingSchoolSupplyForDate(date)
            result.isSuccess shouldBe true
            result.getOrNull() shouldBe setOf(copy)
        }

    "should be able to get the missing school supplies for a date if the user is a student (no missing school supply)" {
        val date = LocalDate.of(2021, 1, 1)
        val reminderForLesson = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = date,
        )
        val desktop = Desktop.create(
            schoolSupplies = setOf(copy),
            schoolSuppliesInBackpack = setOf(copy),
            backpack = "backpack",
        )
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(studentUser)
        coEvery { desktopUseCase.getDesktop() } returns Result.success(desktop)
        coEvery { desktopUseCase.getBookCopy(any()) } returns Result.success(copy)
        coEvery { schoolUseCase.getUserCalendarEventsForDate(date) } returns Result.success(listOf(lessonCalendar))
        val useCase = ReminderUseCase(accessUseCase, desktopUseCase, schoolUseCase, repository)
        val reminder = Reminder.create(setOf(reminderForLesson))
        coEvery { repository.getReminder() } returns reminder
        val result = useCase.remindUserOfMissingSchoolSupplyForDate(date)
        result.isSuccess shouldBe true
        result.getOrNull() shouldBe emptySet()
    }

    "should have an error if the user isn't a student or a professor " +
        "and try to get the missing school supplies for a date" {
            val date = LocalDate.of(2021, 1, 1)
            coEvery { accessUseCase.getLoggedUser() } returns Result.success(notAllowedUser)
            val useCase = ReminderUseCase(accessUseCase, desktopUseCase, schoolUseCase, repository)
            val result = useCase.remindUserOfMissingSchoolSupplyForDate(date)
            result.isFailure shouldBe true
            result.exceptionOrNull() shouldBe ActionNotAllowedForUserException()
        }

    "should be able to subscribe to a reminder if the user is a professor (no missing school supply)" {
        val date = LocalDate.of(2021, 1, 1)
        val reminderForLesson = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = date,
        )
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(studentUser)
        coEvery { desktopUseCase.subscribeToBackpack() } returns Result.success(flow { emit(setOf(copy)) })
        coEvery { desktopUseCase.getBookCopy(any()) } returns Result.success(copy)
        coEvery { schoolUseCase.getUserCalendarEventsForDate(date) } returns Result.success(listOf(lessonCalendar))
        val useCase = ReminderUseCase(accessUseCase, desktopUseCase, schoolUseCase, repository)
        val reminder = Reminder.create(setOf(reminderForLesson))
        coEvery { repository.getReminder() } returns reminder
        val result = useCase.subscribeToRemindUserOfMissingSchoolSupplyForDate(date)
        result.isSuccess shouldBe true
        result.getOrNull()?.collect {
            it shouldBe emptySet()
        }
    }

    "should be able to subscribe to a reminder if the user is a student (a missing school supply)" {
        val date = LocalDate.of(2021, 1, 1)
        val reminderForLesson = ReminderForLessonDate.create(
            isbn = isbn,
            lesson = lesson,
            date = date,
        )
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(studentUser)
        coEvery { desktopUseCase.subscribeToBackpack() } returns Result.success(flow { emit(emptySet()) })
        coEvery { desktopUseCase.getBookCopy(any()) } returns Result.success(copy)
        coEvery { schoolUseCase.getUserCalendarEventsForDate(date) } returns Result.success(listOf(lessonCalendar))
        val useCase = ReminderUseCase(accessUseCase, desktopUseCase, schoolUseCase, repository)
        val reminder = Reminder.create(setOf(reminderForLesson))
        coEvery { repository.getReminder() } returns reminder
        val result = useCase.subscribeToRemindUserOfMissingSchoolSupplyForDate(date)
        result.isSuccess shouldBe true
        result.getOrNull()?.collect {
            it shouldBe setOf(copy)
        }
    }

    "should have an error if the user isn't a student or a professor " +
        "and try to subscribe to a reminder" {
            val date = LocalDate.of(2021, 1, 1)
            coEvery { accessUseCase.getLoggedUser() } returns Result.success(notAllowedUser)
            val useCase = ReminderUseCase(accessUseCase, desktopUseCase, schoolUseCase, repository)
            val result = useCase.subscribeToRemindUserOfMissingSchoolSupplyForDate(date)
            result.isFailure shouldBe true
            result.exceptionOrNull() shouldBe ActionNotAllowedForUserException()
        }
}) {
    companion object {

        val professorUser = User.build {
            email = "prof@gmail.com"
            name = "prof"
            surname = "prof"
            password = "Prof#1234"
            role = Role.PROFESSOR
        }
        val studentUser = User.build {
            email = "student@gmail.com"
            name = "student"
            surname = "student"
            password = "Student#1234"
            role = Role.STUDENT
        }
        val notAllowedUser = User.build {
            email = "normal@gmail.com"
            name = "normal"
            surname = "normal"
            password = "Normal#1234"
            role = Role.USER
        }
        private const val className = "1A"
        private const val math = "Math"
        const val isbn = "9788843025343"
        private val lesson = EventAdapter.WeekLessonImpl(
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(11, 0),
            dayOfWeek = DayOfWeek.MONDAY,
            fromDate = LocalDate.of(2021, 1, 1),
            toDate = LocalDate.of(2021, 1, 31),
            subject = math,
        )
        val studentClass = Class.create(className)
        val professor = Professor.create(
            email = professorUser.email,
            name = professorUser.name,
            surname = professorUser.surname,
        )

        val copy = BookCopy.build {
            rfidCode = "FF:FF:FF:FF"
            book = Book.build {
                isbn = this@Companion.isbn
                title = "title"
                authors = setOf("authors")
            }
        }

        val lessonCalendar = CalendarEventFactory.createWeekLesson(
            subject = lesson.subject,
            startTime = lesson.startTime,
            endTime = lesson.endTime,
            professor = professor,
            studentsClass = studentClass,
            fromDate = lesson.fromDate,
            toDate = lesson.toDate,
            day = lesson.dayOfWeek,
        )
    }
}
