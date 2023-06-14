package com.intelligentbackpack.reminderdata.datasource

import calendar.communication.Lesson
import calendar.communication.Subject

interface ReminderRemoteDataSource {

    suspend fun downloadYear(): String
    suspend fun downloadSubjects(): List<Subject>

    suspend fun downloadLessonsForStudent(email: String, year: String): List<Lesson>

    suspend fun downloadLessonsForProfessor(email: String, year: String): List<Lesson>

    suspend fun downloadBooksForLesson(lesson: Lesson): List<String>
}
