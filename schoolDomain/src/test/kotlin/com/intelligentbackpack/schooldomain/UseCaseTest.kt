package com.intelligentbackpack.schooldomain

import com.intelligentbackpack.accessdomain.entities.Role
import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.accessdomain.exceptions.ActionNotAllowedForUserException
import com.intelligentbackpack.accessdomain.usecase.AccessUseCase
import com.intelligentbackpack.schooldomain.entities.Class
import com.intelligentbackpack.schooldomain.entities.School
import com.intelligentbackpack.schooldomain.entities.calendar.CalendarEventFactory
import com.intelligentbackpack.schooldomain.entities.calendar.SchoolCalendar
import com.intelligentbackpack.schooldomain.entities.calendar.alteration.AlterationFactory
import com.intelligentbackpack.schooldomain.entities.person.Professor
import com.intelligentbackpack.schooldomain.entities.person.Student
import com.intelligentbackpack.schooldomain.repository.SchoolRepository
import com.intelligentbackpack.schooldomain.usecase.SchoolUseCase
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class UseCaseTest : StringSpec({

    val accessUseCase = mockk<AccessUseCase>(relaxed = true)
    val repository = mockk<SchoolRepository>(relaxed = true)

    "should have an error when the user is not student or professor" {
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(normalUser)
        val useCase = SchoolUseCase(accessUseCase, repository)
        val result = useCase.downloadSchool()
        result.isFailure shouldBe true
        result.exceptionOrNull() shouldBe ActionNotAllowedForUserException()
        coVerify(exactly = 0) { repository.downloadSchool(any()) }
    }

    "should be able to download the school when the user is student" {
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(studentUser)
        val useCase = SchoolUseCase(accessUseCase, repository)
        val result = useCase.downloadSchool()
        coVerify { repository.downloadSchool(studentUser) }
        result.isSuccess shouldBe true
    }

    "should be able to download the school when the user is professor" {
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(professorUser)
        val useCase = SchoolUseCase(accessUseCase, repository)
        val result = useCase.downloadSchool()
        coVerify { repository.downloadSchool(professorUser) }
        result.isSuccess shouldBe true
    }

    "should be able to get the school view of the user when the user is student" {
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(studentUser)
        val useCase = SchoolUseCase(accessUseCase, repository)
        coEvery { repository.getSchool(studentUser) } returns createSchool()
        val result = useCase.getSchoolViewOfUser()
        coVerify { repository.getSchool(studentUser) }
        result.isSuccess shouldBe true
    }

    "should be able to get the school view of the user when the user is professor" {
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(professorUser)
        val useCase = SchoolUseCase(accessUseCase, repository)
        coEvery { repository.getSchool(professorUser) } returns createSchool()
        val result = useCase.getSchoolViewOfUser()
        coVerify { repository.getSchool(professorUser) }
        result.isSuccess shouldBe true
    }

    "should have an error when the user is not student or professor when getting the school view of the user" {
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(normalUser)
        val useCase = SchoolUseCase(accessUseCase, repository)
        val result = useCase.getSchoolViewOfUser()
        coVerify(exactly = 0) { repository.getSchool(normalUser) }
        result.isFailure shouldBe true
        result.exceptionOrNull() shouldBe ActionNotAllowedForUserException()
    }

    "should be able to get the user event list when the user is student" {
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(studentUser)
        val useCase = SchoolUseCase(accessUseCase, repository)
        coEvery { repository.getSchool(studentUser) } returns createSchool()
        date.dayOfWeek shouldBe DayOfWeek.MONDAY
        val result = useCase.getUserCalendarEventsForDate(date)
        coVerify { repository.getSchool(studentUser) }
        result.isSuccess shouldBe true
        result.getOrNull()!! shouldBe mondayLessons
    }

    "should be able to get the user event list when the user is professor" {
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(professorUser)
        val useCase = SchoolUseCase(accessUseCase, repository)
        coEvery { repository.getSchool(professorUser) } returns createSchool()
        date.dayOfWeek shouldBe DayOfWeek.MONDAY
        val result = useCase.getUserCalendarEventsForDate(date)
        coVerify { repository.getSchool(professorUser) }
        result.isSuccess shouldBe true
        result.getOrNull()!! shouldBe mondayLessons
    }

    "should have an error when the user is not student or professor when getting the user event list" {
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(normalUser)
        val useCase = SchoolUseCase(accessUseCase, repository)
        val result = useCase.getUserCalendarEventsForDate(date)
        coVerify(exactly = 0) { repository.getSchool(normalUser) }
        result.isFailure shouldBe true
        result.exceptionOrNull() shouldBe ActionNotAllowedForUserException()
    }

    "should be able to add an alteration event when the user is professor" {
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(professorUser)
        val useCase = SchoolUseCase(accessUseCase, repository)
        coEvery { repository.getSchool(professorUser) } returns createSchool()
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
        val alterationEvent = AlterationFactory.createNewEvent(newLesson)
        val result = useCase.addAlterationEvent(alterationEvent)
        coVerify { repository.getSchool(professorUser) }
        coVerify(exactly = 1) { repository.addAlterationEvent(alterationEvent) }
        result.isSuccess shouldBe true
    }

    "should have an error when add an alteration event if user is student" {
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(studentUser)
        val useCase = SchoolUseCase(accessUseCase, repository)
        coEvery { repository.getSchool(studentUser) } returns createSchool()
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
        val alterationEvent = AlterationFactory.createNewEvent(newLesson)
        val result = useCase.addAlterationEvent(alterationEvent)
        result.isFailure shouldBe true
        result.exceptionOrNull() shouldBe ActionNotAllowedForUserException()
    }

    "should have an error when add an alteration event if user is normal user" {
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(normalUser)
        val useCase = SchoolUseCase(accessUseCase, repository)
        coEvery { repository.getSchool(normalUser) } returns createSchool()
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
        val alterationEvent = AlterationFactory.createNewEvent(newLesson)
        val result = useCase.addAlterationEvent(alterationEvent)
        result.isFailure shouldBe true
        result.exceptionOrNull() shouldBe ActionNotAllowedForUserException()
    }

    "should be able to get all the school event list when the user is professor" {
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(professorUser)
        val useCase = SchoolUseCase(accessUseCase, repository)
        var school = createSchool()
        val newLesson = CalendarEventFactory.createDateLesson(
            subject = math,
            startTime = LocalTime.of(11, 30),
            endTime = LocalTime.of(12, 30),
            professor = professor,
            studentsClass = studentClass,
            date = LocalDate.of(2022, 10, 18),
        )
        val alterationEvent = AlterationFactory.createNewEvent(newLesson)
        school = school.calendar?.addAlteration(alterationEvent)?.let { school.replaceCalendar(it) }!!
        coEvery { repository.getSchool(professorUser) } returns school
        val result = useCase.userAllSchoolEvents()
        coVerify { repository.getSchool(professorUser) }
        result.isSuccess shouldBe true
        result.getOrNull()!! shouldBe mondayLessons + tuesday + newLesson
    }

    "should have an error when get all the school event list if user is student" {
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(studentUser)
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(professorUser)
        val useCase = SchoolUseCase(accessUseCase, repository)
        var school = createSchool()
        val newLesson = CalendarEventFactory.createDateLesson(
            subject = math,
            startTime = LocalTime.of(11, 30),
            endTime = LocalTime.of(12, 30),
            professor = professor,
            studentsClass = studentClass,
            date = LocalDate.of(2022, 10, 18),
        )
        val alterationEvent = AlterationFactory.createNewEvent(newLesson)
        school = school.calendar?.addAlteration(alterationEvent)?.let { school.replaceCalendar(it) }!!
        coEvery { repository.getSchool(studentUser) } returns school
        val result = useCase.userAllSchoolEvents()
        result.isSuccess shouldBe true
        result.getOrNull()!! shouldBe mondayLessons + tuesday + newLesson
    }

    "should have an error when get all the school event list if user is normal user" {
        coEvery { accessUseCase.getLoggedUser() } returns Result.success(normalUser)
        val useCase = SchoolUseCase(accessUseCase, repository)
        coEvery { repository.getSchool(normalUser) } returns createSchool()
        val result = useCase.userAllSchoolEvents()
        result.isFailure shouldBe true
        result.exceptionOrNull() shouldBe ActionNotAllowedForUserException()
    }
}) {
    companion object {

        val normalUser = User.build {
            name = "normalUser"
            surname = "normalUser"
            email = "normalUser@gmail.com"
            password = "normalUser#1234"
            role = Role.USER
        }
        val studentUser = User.build {
            name = "studentUser"
            surname = "studentUser"
            email = "studentUser@gmail.com"
            password = "studentUser#1234"
            role = Role.STUDENT
        }
        val professorUser = User.build {
            name = "professorUser"
            surname = "professorUser"
            email = "professorUser@gmail.com"
            password = "professorUser#1234"
            role = Role.PROFESSOR
        }
        private const val schoolYear = "2022-2023"
        private val calendar = SchoolCalendar.create(schoolYear)
        private val school = School.create("School", "City")
            .replaceCalendar(calendar)
        val studentClass = Class.create("1A", school)

        val date: LocalDate = LocalDate.of(2022, 9, 12)
        const val math = "Math"
        const val physics = "Physics"
        val professor = Professor.create(
            email = professorUser.email,
            name = professorUser.name,
            surname = professorUser.surname,
            professorClasses = mapOf(studentClass to setOf(math, physics)),
        )
        private val singleMondayLesson = CalendarEventFactory.createWeekLesson(
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
            CalendarEventFactory.createWeekLesson(
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
            CalendarEventFactory.createWeekLesson(
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

        private fun createSchool(): School {
            val student = Student.create(
                email = studentUser.email,
                name = studentUser.name,
                surname = studentUser.surname,
                studentClass = studentClass,
            )
            val lessons = mondayLessons + tuesday
            return school
                .addClass(studentClass.addStudent(student))
                .addProfessor(professor)
                .addStudent(student)
                .replaceCalendar(calendar.addLessons(lessons))
        }
    }
}
