package com.intelligentbackpack.schooldata.api

import calendar.communication.BasicMessage
import calendar.communication.BooksForLesson
import calendar.communication.ChangeLessonBookPeriodDate
import calendar.communication.Lessons
import calendar.communication.Subjects
import calendar.communication.UserInformations
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

/**
 * API for the calendar microservice.
 */
interface CalendarApi {

    /**
     * Gets all the years.
     *
     * @return the years as a [BasicMessage], field [BasicMessage.message2List] contains the years.
     */
    @GET("/utility/getAllYears")
    fun getYears(): Call<BasicMessage>

    /**
     * Gets all the subjects.
     *
     * @return the subjects as a [Subjects].
     */
    @GET("/utility/getAllSubjects")
    fun getSubjects(): Call<Subjects>

    /**
     * Gets all the lessons for a student.
     *
     * @param email the email of the student.
     * @param year the year.
     * @return the lessons as a [Lessons].
     */
    @GET("/utility/lessons/Student")
    fun getLessonsForStudent(@Query("email") email: String, @Query("year") year: String): Call<Lessons>

    /**
     * Gets all the lessons for a professor.
     *
     * @param email the email of the professor.
     * @param year the year.
     * @return the lessons as a [Lessons].
     */
    @GET("/utility/lessons/Professor")
    suspend fun getLessonsForProfessor(@Query("email") email: String, @Query("year") year: String): Call<Lessons>

    /**
     * Gets the information of a professor.
     *
     * @param email the email of the professor.
     * @param year the year.
     * @return the informations as a [UserInformations].
     */
    @GET("/utility/getProfessorInformations")
    fun getProfessorInformation(@Query("email") email: String, @Query("year") year: String): Call<UserInformations>

    /**
     * Gets the information of a student.
     *
     * @param email the email of the student.
     * @param year the year.
     * @return the informations as a [UserInformations].
     */
    @GET("/utility/getStudentInformations")
    fun getStudentInformation(@Query("email") email: String, @Query("year") year: String): Call<UserInformations>

    /**
     * Gets the lessons for a class.
     *
     * @param email the email of the professor.
     * @param year the year.
     * @param schoolName the name of the school.
     * @param schoolCity the city of the school.
     * @return the lessons as a [Lessons].
     */
    @GET("/utility/lessons")
    fun getLessonsForClass(
        @Query("email") email: String,
        @Query("year") year: String,
        @Query("istitutoNome") schoolName: String,
        @Query("istitutoCitta") schoolCity: String,
    ): Call<Lessons>

    /**
     * Gets the class from a lesson.
     *
     * @param lesson the lesson.
     * @return the class as a [BasicMessage], field [BasicMessage.message] contains the class.
     */
    @POST("/utility/getClass_ByLesson/")
    fun getClassByLesson(@Body lesson: RequestBody): Call<BasicMessage>

    /**
     * Gets the books for a lesson.
     *
     * @param lessons the lessons.
     * @return the books as a [BasicMessage], field [BasicMessage.message2List] contains the books.
     */
    @POST("/utility/booksforLesson/")
    fun getBooksForLesson(@Body lessons: RequestBody): Call<BasicMessage>

    /**
     * Creates a reminder for a lesson.
     *
     * @param remainderForLesson the reminder.
     * @return the result as a [BasicMessage].
     */
    @PUT("/create/bookForLesson/")
    fun createReminderForLesson(@Body remainderForLesson: RequestBody): Call<BasicMessage>

    /**
     * Deletes a reminder for a lesson.
     *
     * @param remainderForLesson the reminder.
     * @return the result as a [BasicMessage].
     */
    @HTTP(method = "DELETE", path = "/remove/bookForLesson", hasBody = true)
    fun deleteReminderForLesson(@Body remainderForLesson: BooksForLesson): Call<BasicMessage>

    /**
     * Modifies a reminder for a lesson, its period of validity.
     *
     * @param remainderForLesson the object containing the reminder and the new period of validity.
     * @return the result as a [BasicMessage].
     */
    @POST("/modify/bookForTimePeriod/")
    fun modifyReminderForLesson(@Body remainderForLesson: ChangeLessonBookPeriodDate): Call<BasicMessage>
}
