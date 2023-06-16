package com.intelligentbackpack.schooldata.datasource

import com.intelligentbackpack.schooldata.db.SchoolDatabase
import com.intelligentbackpack.schooldata.db.entities.Lesson
import com.intelligentbackpack.schooldata.db.entities.Professor
import com.intelligentbackpack.schooldata.db.entities.SchoolClass
import com.intelligentbackpack.schooldata.db.entities.Subject
import com.intelligentbackpack.schooldata.db.entities.Teach
import com.intelligentbackpack.schooldata.storage.SchoolStorage
import java.time.LocalDate
import java.time.LocalTime

class SchoolLocalDataSourceImpl(
    private val database: SchoolDatabase,
    private val storage: SchoolStorage,
) : SchoolLocalDataSource {
    override suspend fun getSubjects(): List<Subject> {
        return database.schoolDao().getSubjects()
    }

    override suspend fun insertSubject(subject: Subject) {
        database.schoolDao().insertSubject(subject)
    }

    override suspend fun insertLesson(lesson: Lesson) {
        database.schoolDao().insertLesson(lesson)
    }

    override suspend fun insertClass(schoolClass: SchoolClass) {
        database.schoolDao().insertClass(schoolClass)
    }

    override suspend fun insertProfessor(professor: Professor) {
        database.schoolDao().insertProfessor(professor)
    }

    override suspend fun insertTeach(teach: Teach) =
        database.schoolDao().insertTeach(teach)

    override suspend fun getLesson(
        day: Int,
        startTime: LocalTime,
        endTime: LocalTime,
        fromDate: LocalDate,
        toDate: LocalDate,
    ): Lesson? {
        return database.schoolDao().getLesson(
            day,
            startTime,
            endTime,
            fromDate,
            toDate,
        )
    }

    override suspend fun getLessons(): List<Lesson> {
        return database.schoolDao().getLessons()
    }

    override suspend fun deleteData() {
        database.schoolDao().deleteLessons()
    }

    override suspend fun saveSchool(school: String, city: String) {
        storage.saveSchool(school, city)
    }

    override suspend fun getSchool(): String {
        return storage.getSchool()
    }

    override suspend fun getCity(): String {
        return storage.getCity()
    }

    override suspend fun isStudent(): Boolean {
        return storage.isStudent()
    }

    override suspend fun saveClass(name: String) {
        storage.saveClass(name)
    }

    override suspend fun getUserClass(): String {
        return storage.getClass()
    }

    override suspend fun saveYear(year: String) {
        storage.saveYear(year)
    }

    override suspend fun getYear(): String {
        return storage.getYear()
    }

    override suspend fun getClasses(): List<SchoolClass> {
        return database.schoolDao().getClasses()
    }

    override suspend fun getProfessors(): List<Professor> {
        return database.schoolDao().getProfessors()
    }

    override suspend fun getTeaches(): List<Teach> {
        return database.schoolDao().getTeaches()
    }
}
