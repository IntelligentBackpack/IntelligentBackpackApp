package com.intelligentbackpack.reminderdata.datasource

import calendar.communication.Lesson
import com.intelligentbackpack.networkutility.DownloadException
import com.intelligentbackpack.networkutility.ErrorHandler
import com.intelligentbackpack.networkutility.RetrofitHelper
import com.intelligentbackpack.schooldata.api.CalendarApi
import com.intelligentbackpack.schooldata.datasource.SchoolRemoteDataSourceImpl
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
    private val schoolRemoteDataSource = SchoolRemoteDataSourceImpl(baseUrl)
    override suspend fun downloadYear() = schoolRemoteDataSource.downloadYear()

    override suspend fun downloadSubjects() = schoolRemoteDataSource.downloadSubjects()

    override suspend fun downloadLessonsForStudent(email: String, year: String) =
        schoolRemoteDataSource.downloadLessonsForStudent(email, year)

    override suspend fun downloadLessonsForProfessor(email: String, year: String) =
        schoolRemoteDataSource.downloadLessonsForProfessor(email, year)

    override suspend fun downloadBooksForLesson(lesson: Lesson): List<String> {
        val response = calendarApi
            .getBooksForLesson(schoolRemoteDataSource.createJsonForLesson(lesson))
            .execute()
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
        val lessonJson = JSONObject(jsonParam)
        val isbnJson = JSONArray(listOf(isbn))
        val jsonParamReminder = mapOf(
            ("lesson" to lessonJson),
            ("ISBNs" to isbnJson),
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
