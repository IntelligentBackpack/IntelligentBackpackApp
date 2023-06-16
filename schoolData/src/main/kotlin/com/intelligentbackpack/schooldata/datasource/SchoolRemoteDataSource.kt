package com.intelligentbackpack.schooldata.datasource

import calendar.communication.Lesson
import calendar.communication.Subject
import calendar.communication.UserInformations

interface SchoolRemoteDataSource {

    suspend fun downloadYear(): String
    suspend fun downloadSubjects(): List<Subject>

    suspend fun downloadLessonsForStudent(email: String, year: String): List<Lesson>

    suspend fun downloadLessonsForProfessor(email: String, year: String): List<Lesson>
    suspend fun downloadStudent(email: String, year: String): UserInformations

    suspend fun downloadProfessor(email: String, year: String): UserInformations

    suspend fun getClass(lesson: Lesson): String
}
