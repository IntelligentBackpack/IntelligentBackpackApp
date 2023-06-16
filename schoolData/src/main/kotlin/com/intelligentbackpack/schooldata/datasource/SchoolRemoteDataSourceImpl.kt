package com.intelligentbackpack.schooldata.datasource

import calendar.communication.Lesson
import calendar.communication.Subject
import calendar.communication.UserInformations
import com.intelligentbackpack.networkutility.DownloadException
import com.intelligentbackpack.networkutility.ErrorHandler
import com.intelligentbackpack.networkutility.RetrofitHelper
import com.intelligentbackpack.schooldata.api.CalendarApi

class SchoolRemoteDataSourceImpl(
    baseUrl: String,
) : SchoolRemoteDataSource {
    private val calendarApi = RetrofitHelper.getInstance(baseUrl).create(CalendarApi::class.java)
    override suspend fun downloadYear(): String {
        val response = calendarApi.getYears().execute()
        if (response.isSuccessful) {
            return response.body()?.message2List?.maxOf { it } ?: ""
        } else {
            throw DownloadException(ErrorHandler.getError(response))
        }
    }

    override suspend fun downloadSubjects(): List<Subject> {
        val response = calendarApi.getSubjects().execute()
        if (response.isSuccessful) {
            return response.body()?.subjectsList ?: emptyList()
        } else {
            throw DownloadException(ErrorHandler.getError(response))
        }
    }

    override suspend fun downloadLessonsForStudent(email: String, year: String): List<Lesson> {
        val response = calendarApi.getLessonsForStudent(email, year).execute()
        if (response.isSuccessful) {
            return response.body()?.lessonsList ?: emptyList()
        } else {
            throw DownloadException(ErrorHandler.getError(response))
        }
    }

    override suspend fun downloadLessonsForProfessor(email: String, year: String): List<Lesson> {
        val response = calendarApi.getLessonsForProfessor(email, year).execute()
        if (response.isSuccessful) {
            return response.body()?.lessonsList ?: emptyList()
        } else {
            throw DownloadException(ErrorHandler.getError(response))
        }
    }

    override suspend fun downloadStudent(email: String, year: String): UserInformations {
        val response = calendarApi.getStudentInformations(email, year).execute()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw DownloadException(ErrorHandler.getError(response))
        }
    }

    override suspend fun downloadProfessor(email: String, year: String): UserInformations {
        val response = calendarApi.getProfessorInformations(email, year).execute()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw DownloadException(ErrorHandler.getError(response))
        }
    }

    override suspend fun getClass(lesson: Lesson): String {
        val response = calendarApi.getClassByLesson(lesson).execute()
        if (response.isSuccessful) {
            return response.body()?.message ?: ""
        } else {
            throw DownloadException(ErrorHandler.getError(response))
        }
    }
}
