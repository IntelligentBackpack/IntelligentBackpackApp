package com.intelligentbackpack.schooldata.datasource

import calendar.communication.Lesson
import calendar.communication.Subject
import calendar.communication.UserInformations

/**
 * Interface for the remote data source.
 */
interface SchoolRemoteDataSource {

    /**
     * Downloads the year.
     *
     * @return the year.
     */
    suspend fun downloadYear(): String

    /**
     * Downloads the subjects.
     *
     * @return the subjects.
     */
    suspend fun downloadSubjects(): List<Subject>

    /**
     * Downloads the lessons for a student.
     *
     * @param email the email of the student.
     * @param year the year.
     * @return the lessons.
     */
    suspend fun downloadLessonsForStudent(email: String, year: String): List<Lesson>

    /**
     * Downloads the lessons for a professor.
     *
     * @param email the email of the professor.
     * @param year the year.
     * @return the lessons.
     */
    suspend fun downloadLessonsForProfessor(email: String, year: String): List<Lesson>

    /**
     * Downloads the student information.
     *
     * @param email the email of the student.
     * @param year the year.
     * @return the student information.
     */
    suspend fun downloadStudent(email: String, year: String): UserInformations

    /**
     * Downloads the professor information.
     *
     * @param email the email of the professor.
     * @param year the year.
     * @return the professor information.
     */
    suspend fun downloadProfessor(email: String, year: String): UserInformations

    /**
     * Downloads the class of a lesson.
     *
     * @param lesson the lesson.
     * @return the class.
     */
    suspend fun getClass(lesson: Lesson): String
}
