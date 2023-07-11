package com.intelligentbackpack.reminderdata.datasource

import calendar.communication.Lesson
import com.intelligentbackpack.networkutility.DownloadException
import com.intelligentbackpack.networkutility.ErrorHandler
import com.intelligentbackpack.networkutility.RetrofitHelper
import com.intelligentbackpack.schooldata.api.CalendarApi
import com.intelligentbackpack.schooldata.datasource.SchoolRemoteDataSourceImpl
import okhttp3.RequestBody
import org.json.JSONObject
import java.time.LocalDate

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

    private fun createJsonForReminder(email: String, lesson: Lesson, isbn: String): RequestBody {
        val json = JSONObject()
        json.put("email_executor", email)
        json.put("lesson", schoolRemoteDataSource.createJsonForLesson(lesson))
        json.put("isbn", isbn)
        return RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            json.toString(),
        )
    }

    override suspend fun createNewReminderForLesson(email: String, lesson: Lesson, isbn: String): String {
        val response = calendarApi.createReminderForLesson(createJsonForReminder(email, lesson, isbn)).execute()
        if (response.isSuccessful) {
            return response.body()?.message ?: ""
        } else {
            throw DownloadException(ErrorHandler.getError(response))
        }
    }

    override suspend fun deleteReminderForLesson(email: String, lesson: Lesson, isbn: String) {
        val response = calendarApi.deleteReminderForLesson(createJsonForReminder(email, lesson, isbn)).execute()
        if (!response.isSuccessful) {
            throw DownloadException(ErrorHandler.getError(response))
        }
    }

    override suspend fun changeReminderForLesson(
        email: String,
        lesson: Lesson,
        isbn: String,
        fromDate: LocalDate,
        toDate: LocalDate,
    ) {
        val json = JSONObject()
        json.put("email_executor", email)
        json.put("lesson", schoolRemoteDataSource.createJsonForLesson(lesson))
        json.put("isbn", isbn)
        json.put("nuovaInizioData", fromDate.toString())
        json.put("nuovaFineData", toDate.toString())
        val response = calendarApi.modifyReminderForLesson(
            RequestBody.create(
                okhttp3.MediaType.parse("application/json; charset=utf-8"),
                json.toString(),
            ),
        ).execute()
        if (!response.isSuccessful) {
            throw DownloadException(ErrorHandler.getError(response))
        }
    }
}
