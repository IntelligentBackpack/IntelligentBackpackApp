package com.intelligentbackpack.reminderdata.datasource

import calendar.communication.Lesson
import calendar.communication.Subject
import com.intelligentbackpack.networkutility.DownloadException
import com.intelligentbackpack.networkutility.ErrorHandler
import com.intelligentbackpack.networkutility.RetrofitHelper
import com.intelligentbackpack.schooldata.api.CalendarApi
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject

/**
 * Implementation of Remote data source for the reminder module.
 *
 * @param baseUrl the base url of the api.
 */
class ReminderRemoteDataSourceImpl(
    baseUrl: String,
) : ReminderRemoteDataSource {
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

    override suspend fun downloadBooksForLesson(lesson: Lesson): List<String> {
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
        val response = calendarApi.getBooksForLesson(request).execute()
        if (response.isSuccessful) {
            return response.body()?.message2List ?: emptyList()
        } else {
            throw DownloadException(ErrorHandler.getError(response))
        }
    }

    override suspend fun createNewReminderForLesson(email: String, lesson: Lesson, isbn: String): String {
        val jsonParam = mapOf(
            ("Email" to email),
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
        val lessonjson = JSONObject(jsonParam)
        val isbnjson = JSONArray(listOf(isbn))
        val jsonParamReminder = mapOf(
            ("lesson" to lessonjson),
            ("ISBNs" to isbnjson),
            ("email_executor" to email),
        )
        val request = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            (JSONObject(jsonParamReminder)).toString(),
        )
        val response = calendarApi.createReminderForLesson(request).execute()
        if (response.isSuccessful) {
            return response.body()?.message ?: ""
        } else {
            throw DownloadException(ErrorHandler.getError(response))
        }
    }
}
