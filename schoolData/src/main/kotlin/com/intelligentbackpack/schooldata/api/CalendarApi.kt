package com.intelligentbackpack.schooldata.api

import calendar.communication.BasicMessage
import calendar.communication.BooksForLesson
import calendar.communication.ChangeLessonBookPeriodDate
import calendar.communication.Lesson
import calendar.communication.Lessons
import calendar.communication.Subjects
import calendar.communication.UserInformations
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Query

interface CalendarApi {
    @GET("/utility/getAllYears")
    fun getYears(): Call<BasicMessage>

    @GET("/utility/getAllSubjects")
    fun getSubjects(): Call<Subjects>

    @GET("/utility/lessons/Student")
    fun getLessonsForStudent(@Query("email") email: String, @Query("year") year: String): Call<Lessons>

    @GET("/utility/lessons/Professor")
    suspend fun getLessonsForProfessor(@Query("email") email: String, @Query("year") year: String): Call<Lessons>

    @GET("/utility/getProfessorInformations")
    fun getProfessorInformations(@Query("email") email: String, @Query("year") year: String): Call<UserInformations>

    @GET("/utility/getStudentInformations")
    fun getStudentInformations(@Query("email") email: String, @Query("year") year: String): Call<UserInformations>

    @GET("/utility/lessons")
    fun getLessonsForClass(
        @Query("email") email: String,
        @Query("year") year: String,
        @Query("istitutoNome") schoolName: String,
        @Query("istitutoCitta") schoolCity: String,
    ): Call<Lessons>

    @POST("/utility/getClass_ByLesson")
    fun getClassByLesson(@Body lesson: Lesson): Call<BasicMessage>

    @POST("/utility/booksforLesson/")
    fun getBooksForLesson(@Body lessons: Lesson): Call<BooksForLesson>

    @HTTP(method = "DELETE", path = "/remove/bookForLesson", hasBody = true)
    fun deleteReminderForLesson(@Body remainderForLesson: BooksForLesson)

    @POST("/modify/bookForTimePeriod/")
    fun modifyReminderForLesson(@Body remainderForLesson: ChangeLessonBookPeriodDate)
}
