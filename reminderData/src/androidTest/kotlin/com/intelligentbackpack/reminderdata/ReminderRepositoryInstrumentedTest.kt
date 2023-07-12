package com.intelligentbackpack.reminderdata

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import calendar.communication.Lesson
import calendar.communication.Subject
import com.intelligentbackpack.accessdomain.entities.Role
import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.reminderdata.adapter.LessonAdapter.fromDBToDomain
import com.intelligentbackpack.reminderdata.datasource.ReminderDomainRepositoryImpl
import com.intelligentbackpack.reminderdata.datasource.ReminderLocalDataSourceImpl
import com.intelligentbackpack.reminderdata.datasource.ReminderRemoteDataSource
import com.intelligentbackpack.reminderdata.db.ReminderDatabaseHelper
import com.intelligentbackpack.reminderdata.db.entities.Reminder
import com.intelligentbackpack.reminderdomain.entitites.implementation.ReminderForLessonDateImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import com.intelligentbackpack.reminderdata.db.entities.Lesson as DBLesson
import com.intelligentbackpack.reminderdata.db.entities.Subject as DBSubject

/**
 * Instrumented test for reminder repository
 *
 */
@RunWith(AndroidJUnit4::class)
class ReminderRepositoryInstrumentedTest {

    private val professorUser = User.build {
        email = "prof@gmail.com"
        name = "prof"
        surname = "prof"
        password = "Prof#1234"
        role = Role.PROFESSOR
    }

    private val year = "2022/2023"

    private val subjects = listOf(
        Subject.newBuilder()
            .setID("1")
            .setName("Math")
            .build(),
        Subject.newBuilder()
            .setID("2")
            .setName("Physics")
            .build(),
    )
    private val dbSubject = DBSubject(
        subjectId = 1,
        name = "Math",
    )
    private val lesson =
        Lesson.newBuilder()
            .setMateria(1)
            .setGiorno("Monday")
            .setDataInizio("2021-10-11")
            .setDataFine("2021-12-11")
            .setOraInizio("08:00")
            .setOraFine("10:00")
            .setNomeLezione("Math")
            .setProfessore(professorUser.email)
            .setIDCalendario(1)
            .build()
    private val dbLesson = DBLesson(
        id = 1,
        subjectId = 1,
        day = DayOfWeek.MONDAY.value,
        fromDate = LocalDate.of(2021, 10, 11),
        toDate = LocalDate.of(2021, 12, 11),
        startTime = LocalTime.of(8, 0),
        endTime = LocalTime.of(10, 0),
        module = "Math",
        professor = professorUser.email,
        calendarId = 1,
    )

    private val isbn = "1234567890123"

    private val remoteDataSource = mockk<ReminderRemoteDataSource>(relaxed = false)

    @Test
    fun downloadData(): Unit = runBlocking {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        coEvery { remoteDataSource.downloadYear() } returns year
        coEvery { remoteDataSource.downloadSubjects() } returns subjects
        coEvery { remoteDataSource.downloadLessonsForProfessor(any(), any()) } returns listOf(lesson)
        coEvery { remoteDataSource.downloadBooksForLesson(any()) } returns listOf(isbn)
        val db = ReminderDatabaseHelper.getDatabase(appContext)
        val localDataSource = ReminderLocalDataSourceImpl(db)
        val repository = ReminderDomainRepositoryImpl(remoteDataSource, localDataSource)
        repository.downloadReminder(professorUser)
        val lessons = localDataSource.getLessons()
        assert(lessons.isNotEmpty())
        val books = localDataSource.getReminders()
        assertEquals(books.size, 1)
        assertEquals(books[0].isbn, isbn)
    }

    @Test
    fun getReminder(): Unit = runBlocking {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val db = ReminderDatabaseHelper.getDatabase(appContext)
        val localDataSource = ReminderLocalDataSourceImpl(db)
        val repository = ReminderDomainRepositoryImpl(remoteDataSource, localDataSource)
        localDataSource.insertSubject(dbSubject)
        localDataSource.saveLesson(dbLesson)
        localDataSource.saveReminder(Reminder(0, dbLesson.id, isbn, dbLesson.fromDate, dbLesson.toDate))
        val reminders = repository.getReminder()
        assertEquals(reminders.getLessonsForBook(isbn).size, 1)
        assertEquals(reminders.getBooksForLesson(dbLesson.fromDBToDomain(listOf(dbSubject))).size, 1)
    }

    @Test
    fun getReminderWithNoData(): Unit = runBlocking {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val db = ReminderDatabaseHelper.getDatabase(appContext)
        val localDataSource = ReminderLocalDataSourceImpl(db)
        val repository = ReminderDomainRepositoryImpl(remoteDataSource, localDataSource)
        val reminders = repository.getReminder()
        assertEquals(reminders.getLessonsForBook(isbn).size, 0)
        assertEquals(reminders.getBooksForLesson(dbLesson.fromDBToDomain(listOf(dbSubject))).size, 0)
    }

    @Test
    fun addReminder(): Unit = runBlocking {
        val date = LocalDate.of(2021, 10, 11)
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        coEvery { remoteDataSource.createNewReminderForLesson(any(), any(), any()) } returns Unit
        val db = ReminderDatabaseHelper.getDatabase(appContext)
        val localDataSource = ReminderLocalDataSourceImpl(db)
        val repository = ReminderDomainRepositoryImpl(remoteDataSource, localDataSource)
        localDataSource.insertSubject(dbSubject)
        localDataSource.saveLesson(dbLesson)
        repository.addBookForLesson(
            ReminderForLessonDateImpl(
                lesson = dbLesson.fromDBToDomain(listOf(dbSubject)),
                isbn = isbn,
                date = date,
            ),
            professorUser,
        )
        val reminders = localDataSource.getReminders()
        coVerify(exactly = 1) { remoteDataSource.createNewReminderForLesson(professorUser.email, any(), isbn) }
        assertEquals(reminders.size, 1)
        assertEquals(reminders[0].isbn, isbn)
        assertEquals(reminders[0].fromDate, date)
        assertEquals(reminders[0].toDate, date)
    }

    @Test
    fun deleteReminder(): Unit = runBlocking {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        coEvery { remoteDataSource.deleteReminderForLesson(any(), any(), any()) } returns Unit
        val db = ReminderDatabaseHelper.getDatabase(appContext)
        val localDataSource = ReminderLocalDataSourceImpl(db)
        val repository = ReminderDomainRepositoryImpl(remoteDataSource, localDataSource)
        localDataSource.insertSubject(dbSubject)
        localDataSource.saveLesson(dbLesson)
        val date = LocalDate.of(2021, 10, 11)
        localDataSource.saveReminder(Reminder(0, dbLesson.id, isbn, date, date))
        repository.removeBookForLesson(
            ReminderForLessonDateImpl(
                lesson = dbLesson.fromDBToDomain(listOf(dbSubject)),
                isbn = isbn,
                date = date,
            ),
            professorUser,
        )
        val reminders = localDataSource.getReminders()
        coVerify(exactly = 1) { remoteDataSource.deleteReminderForLesson(professorUser.email, any(), isbn) }
        assertEquals(reminders.size, 0)
    }

    @Test
    fun changeReminder(): Unit = runBlocking {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        coEvery { remoteDataSource.changeReminderForLesson(any(), any(), any(), any(), any()) } returns Unit
        val db = ReminderDatabaseHelper.getDatabase(appContext)
        val localDataSource = ReminderLocalDataSourceImpl(db)
        val repository = ReminderDomainRepositoryImpl(remoteDataSource, localDataSource)
        localDataSource.insertSubject(dbSubject)
        localDataSource.saveLesson(dbLesson)
        val date = LocalDate.of(2021, 10, 11)
        localDataSource.saveReminder(Reminder(0, dbLesson.id, isbn, date, date))
        val newDate = LocalDate.of(2021, 11, 12)
        repository.changeBookForLesson(
            ReminderForLessonDateImpl(
                lesson = dbLesson.fromDBToDomain(listOf(dbSubject)),
                isbn = isbn,
                date = date,
            ),
            ReminderForLessonDateImpl(
                lesson = dbLesson.fromDBToDomain(listOf(dbSubject)),
                isbn = isbn,
                date = newDate,
            ),
            professorUser,
        )
        val reminders = localDataSource.getReminders()
        coVerify(exactly = 1) {
            remoteDataSource.changeReminderForLesson(
                professorUser.email,
                any(),
                isbn,
                newDate,
                newDate,
            )
        }
        assertEquals(reminders.size, 1)
        assertEquals(reminders[0].isbn, isbn)
        assertEquals(reminders[0].fromDate, newDate)
        assertEquals(reminders[0].toDate, newDate)
    }
}
