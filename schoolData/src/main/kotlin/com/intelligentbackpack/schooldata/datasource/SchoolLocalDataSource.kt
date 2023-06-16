package com.intelligentbackpack.schooldata.datasource

import com.intelligentbackpack.schooldata.db.entities.Lesson
import com.intelligentbackpack.schooldata.db.entities.Professor
import com.intelligentbackpack.schooldata.db.entities.SchoolClass
import com.intelligentbackpack.schooldata.db.entities.Subject
import com.intelligentbackpack.schooldata.db.entities.Teach
import java.time.LocalDate
import java.time.LocalTime

interface SchoolLocalDataSource {

    suspend fun getSubjects(): List<Subject>

    suspend fun insertSubject(subject: Subject)

    suspend fun insertSubjects(subjects: List<Subject>) {
        subjects.forEach { insertSubject(it) }
    }

    suspend fun insertClass(schoolClass: SchoolClass)

    suspend fun insertProfessor(professor: Professor)

    suspend fun insertTeach(teach: Teach): Long

    suspend fun insertLesson(lesson: Lesson)

    suspend fun getLesson(
        day: Int,
        startTime: LocalTime,
        endTime: LocalTime,
        fromDate: LocalDate,
        toDate: LocalDate,
    ): Lesson?

    suspend fun getLessons(): List<Lesson>

    suspend fun deleteData()

    suspend fun saveSchool(school: String, city: String)

    suspend fun getSchool(): String

    suspend fun getCity(): String

    suspend fun isStudent(): Boolean

    suspend fun saveClass(name: String)

    suspend fun getUserClass(): String
    suspend fun saveYear(year: String)

    suspend fun getYear(): String
    suspend fun getClasses(): List<SchoolClass>
    suspend fun getProfessors(): List<Professor>
    suspend fun getTeaches(): List<Teach>
}
