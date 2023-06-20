package com.intelligentbackpack.schooldata.datasource

import calendar.communication.Lesson
import calendar.communication.Subject
import calendar.communication.UserInformations
import com.intelligentbackpack.networkutility.DownloadException
import com.intelligentbackpack.networkutility.ErrorHandler
import com.intelligentbackpack.networkutility.RetrofitHelper
import com.intelligentbackpack.schooldata.api.CalendarApi
import okhttp3.RequestBody
import org.json.JSONObject

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
        val response = calendarApi.getStudentInformation(email, year).execute()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw DownloadException(ErrorHandler.getError(response))
        }
    }

    override suspend fun downloadProfessor(email: String, year: String): UserInformations {
        val response = calendarApi.getProfessorInformation(email, year).execute()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw DownloadException(ErrorHandler.getError(response))
        }
    }

    override suspend fun getClass(lesson: Lesson): String {
        val jsonParam = mapOf(
            ("Nome_lezione" to lesson.nomeLezione),
            ("Materia" to lesson.materia),
            ("Giorno" to lesson.giorno),
            ("Ora_inizio" to lesson.oraInizio),
            ("Ora_fine" to lesson.oraFine),
            ("Professore" to lesson.professore),
            ("Data_Inizio" to lesson.dataInizio),
            ("Data_Fine" to lesson.dataFine),
            ("ID_Calendario" to lesson.idCalendario),
        )
        val request = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            (JSONObject(jsonParam)).toString(),
        )
        val response = calendarApi.getClassByLesson(request).execute()
        if (response.isSuccessful) {
            return response.body()?.message ?: ""
        } else {
            throw DownloadException(ErrorHandler.getError(response))
        }
    }
}
