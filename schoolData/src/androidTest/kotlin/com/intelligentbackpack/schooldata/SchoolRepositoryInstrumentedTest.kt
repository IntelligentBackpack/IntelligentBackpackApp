package com.intelligentbackpack.schooldata

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import calendar.communication.Lesson
import calendar.communication.Subject
import calendar.communication.UserInformations
import com.intelligentbackpack.accessdomain.entities.Role
import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.schooldata.datasource.SchoolDomainRepositoryImpl
import com.intelligentbackpack.schooldata.datasource.SchoolLocalDataSourceImpl
import com.intelligentbackpack.schooldata.datasource.SchoolRemoteDataSource
import com.intelligentbackpack.schooldata.db.SchoolDatabaseHelper
import com.intelligentbackpack.schooldata.db.entities.SchoolClass
import com.intelligentbackpack.schooldata.db.entities.Teach
import com.intelligentbackpack.schooldata.storage.SchoolStorageImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import com.intelligentbackpack.schooldata.db.entities.Lesson as DBLesson
import com.intelligentbackpack.schooldata.db.entities.Professor as DBProfessor
import com.intelligentbackpack.schooldata.db.entities.Subject as DBSubject

/**
 * Instrumented test for school repository
 *
 */
@RunWith(AndroidJUnit4::class)
class SchoolRepositoryInstrumentedTest {

    private val professorUser = User.build {
        email = "prof@gmail.com"
        name = "prof"
        surname = "prof"
        password = "Prof#1234"
        role = Role.PROFESSOR
    }

    private val studentUser = User.build {
        email = "student@gmail.com"
        name = "student"
        surname = "student"
        password = "Student#1234"
        role = Role.STUDENT
    }

    private val studentClass = "1A"
    private val schoolName = "Liceo Scientifico"
    private val city = "Roma"

    private val professorInformation = UserInformations.newBuilder()
        .setEmailUser(professorUser.email)
        .setNome(professorUser.name)
        .setCognome(professorUser.surname)
        .addClasses(studentClass)
        .addInsitutesCitta(city)
        .addInsitutesName(schoolName)
        .build()

    private val studentInformation = UserInformations.newBuilder()
        .setEmailUser(studentUser.email)
        .setNome(studentUser.name)
        .setCognome(studentUser.surname)
        .addClasses(studentClass)
        .addInsitutesCitta(city)
        .addInsitutesName(schoolName)
        .build()

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
    private val dbMath = DBSubject(
        subjectId = 1,
        name = "Math",
    )
    private val dbPhysics = DBSubject(
        subjectId = 2,
        name = "Physics",
    )
    private val dbSubjects = listOf(dbMath, dbPhysics)
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
        day = DayOfWeek.MONDAY.value,
        fromDate = LocalDate.of(2021, 10, 11),
        toDate = LocalDate.of(2021, 12, 11),
        startTime = LocalTime.of(8, 0),
        endTime = LocalTime.of(10, 0),
        module = "Math",
        teachId = 1,
    )
    private val teach = Teach(
        id = 1,
        subjectId = 1,
        professorEmail = professorUser.email,
        schoolClass = studentClass,
    )

    private val dbClass = SchoolClass(
        name = studentClass,
        calendarId = 1,
    )

    private val dbProfessor = DBProfessor(
        email = professorUser.email,
        name = professorUser.name,
        surname = professorUser.surname,
    )

    private val remoteDataSource = mockk<SchoolRemoteDataSource>(relaxed = false)

    @Test
    fun downloadDataProfessor(): Unit = runBlocking {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        coEvery { remoteDataSource.downloadYear() } returns year
        coEvery { remoteDataSource.downloadSubjects() } returns subjects
        coEvery { remoteDataSource.downloadProfessor(any(), any()) } returns professorInformation
        coEvery { remoteDataSource.downloadLessonsForProfessor(any(), any()) } returns listOf(lesson)
        coEvery { remoteDataSource.getClass(any()) } returns studentClass
        val db = SchoolDatabaseHelper.getDatabase(appContext)
        val storage = SchoolStorageImpl(appContext)
        val localDataSource = SchoolLocalDataSourceImpl(db, storage)
        val repository = SchoolDomainRepositoryImpl(remoteDataSource, localDataSource)
        repository.downloadSchool(professorUser)
        assertEquals(year, storage.getYear())
        assertEquals(city, storage.getCity())
        assertEquals(schoolName, storage.getSchool())
        assertEquals(dbSubjects, db.schoolDao().getSubjects())
        assertEquals(listOf(dbLesson), db.schoolDao().getLessons())
        assertEquals(listOf(dbClass), db.schoolDao().getClasses())
        assertEquals(listOf(teach), db.schoolDao().getTeaches())
        assertEquals(listOf(dbProfessor), db.schoolDao().getProfessors())
        assert(!storage.isStudent())
    }

    @Test
    fun getSchoolProfessor(): Unit = runBlocking {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val db = SchoolDatabaseHelper.getDatabase(appContext)
        val storage = SchoolStorageImpl(appContext)
        val localDataSource = SchoolLocalDataSourceImpl(db, storage)
        val repository = SchoolDomainRepositoryImpl(remoteDataSource, localDataSource)
        storage.saveSchool(schoolName, city)
        storage.saveYear(year)
        localDataSource.insertSubjects(dbSubjects)
        localDataSource.insertProfessor(dbProfessor)
        localDataSource.insertClass(dbClass)
        localDataSource.insertTeach(teach)
        localDataSource.insertLesson(dbLesson)
        val school = repository.getSchool(professorUser)
        assertEquals(city, school.city)
        assertEquals(schoolName, school.name)
        assertEquals(year, school.calendar!!.schoolYear)
        assertEquals(listOf(studentClass), school.classes.map { it.name })
        assertEquals(listOf(professorUser.email), school.professors.map { it.email })
    }

    @Test
    fun downloadDataStudent(): Unit = runBlocking {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        coEvery { remoteDataSource.downloadYear() } returns year
        coEvery { remoteDataSource.downloadSubjects() } returns subjects
        coEvery { remoteDataSource.downloadStudent(any(), any()) } returns studentInformation
        coEvery { remoteDataSource.downloadProfessor(any(), any()) } returns professorInformation
        coEvery { remoteDataSource.downloadLessonsForStudent(any(), any()) } returns listOf(lesson)
        coEvery { remoteDataSource.getClass(any()) } returns studentClass
        val db = SchoolDatabaseHelper.getDatabase(appContext)
        val storage = SchoolStorageImpl(appContext)
        val localDataSource = SchoolLocalDataSourceImpl(db, storage)
        val repository = SchoolDomainRepositoryImpl(remoteDataSource, localDataSource)
        repository.downloadSchool(studentUser)
        assertEquals(year, storage.getYear())
        assertEquals(city, storage.getCity())
        assertEquals(schoolName, storage.getSchool())
        assertEquals(dbSubjects, db.schoolDao().getSubjects())
        assertEquals(listOf(dbLesson), db.schoolDao().getLessons())
        assertEquals(listOf(dbClass), db.schoolDao().getClasses())
        assertEquals(listOf(teach), db.schoolDao().getTeaches())
        assertEquals(listOf(dbProfessor), db.schoolDao().getProfessors())
        assertEquals(studentClass, storage.getClass())
        assert(storage.isStudent())
    }

    @Test
    fun getSchoolStudent(): Unit = runBlocking {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val db = SchoolDatabaseHelper.getDatabase(appContext)
        val storage = SchoolStorageImpl(appContext)
        val localDataSource = SchoolLocalDataSourceImpl(db, storage)
        val repository = SchoolDomainRepositoryImpl(remoteDataSource, localDataSource)
        storage.saveSchool(schoolName, city)
        storage.saveYear(year)
        storage.saveClass(studentClass)
        localDataSource.insertSubjects(dbSubjects)
        localDataSource.insertProfessor(dbProfessor)
        localDataSource.insertClass(dbClass)
        localDataSource.insertTeach(teach)
        localDataSource.insertLesson(dbLesson)
        val school = repository.getSchool(studentUser)
        assertEquals(city, school.city)
        assertEquals(schoolName, school.name)
        assertEquals(year, school.calendar!!.schoolYear)
        assertEquals(listOf(studentClass), school.classes.map { it.name })
        assertEquals(listOf(professorUser.email), school.professors.map { it.email })
        assertEquals(listOf(studentUser.email), school.students.map { it.email })
        assertEquals(school.classes.first().students.map { it.email }, listOf(studentUser.email))
    }
}
