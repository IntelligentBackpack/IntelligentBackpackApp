package com.intelligentbackpack.schooldata.datasource

import com.intelligentbackpack.schooldata.db.entities.Lesson
import com.intelligentbackpack.schooldata.db.entities.Professor
import com.intelligentbackpack.schooldata.db.entities.SchoolClass
import com.intelligentbackpack.schooldata.db.entities.Subject
import com.intelligentbackpack.schooldata.db.entities.Teach
import java.time.LocalDate
import java.time.LocalTime

/**
 * Interface for the local data source.
 */
interface SchoolLocalDataSource {

    /**
     * Gets all the subjects.
     *
     * @return the subjects.
     */
    suspend fun getSubjects(): List<Subject>

    /**
     * Inserts a subject.
     *
     * @param subject the subject to add.
     */
    suspend fun insertSubject(subject: Subject)

    /**
     * Inserts a list of subjects.
     *
     * @param subjects the subjects to add.
     */
    suspend fun insertSubjects(subjects: List<Subject>) {
        subjects.forEach { insertSubject(it) }
    }

    /**
     * Inserts a class.
     *
     * @param schoolClass the class to add.
     */
    suspend fun insertClass(schoolClass: SchoolClass)

    /**
     * Inserts a professor.
     *
     * @param professor the professor to add.
     */
    suspend fun insertProfessor(professor: Professor)

    /**
     * Inserts a teaching relationship.
     *
     * @param teach the teach to add.
     * @return the id of the inserted teach.
     */
    suspend fun insertTeach(teach: Teach): Long

    /**
     * Inserts a lesson.
     *
     * @param lesson the lesson to add.
     */
    suspend fun insertLesson(lesson: Lesson)

    /**
     * Gets the lesson for the day, time and date.
     *
     * @param day the day of the week of the lessons.
     * @param startTime the start time of the lessons.
     * @param endTime the end time of the lessons.
     * @param fromDate the start date of the lessons.
     * @param toDate the end date of the lessons.
     */
    suspend fun getLesson(
        day: Int,
        startTime: LocalTime,
        endTime: LocalTime,
        fromDate: LocalDate,
        toDate: LocalDate,
    ): Lesson?

    /**
     * Gets the lessons.
     *
     * @return the lessons.
     */
    suspend fun getLessons(): List<Lesson>

    /**
     * Deletes all the data.
     */
    suspend fun deleteData()

    /**
     * Saves the school and the city.
     *
     * @param school the school to save.
     * @param city the city to save.
     */
    suspend fun saveSchool(school: String, city: String)

    /**
     * Returns the school.
     *
     * @return the school.
     */
    suspend fun getSchool(): String

    /**
     * Returns the city of the school.
     *
     * @return the city.
     */
    suspend fun getCity(): String

    /**
     * Checks if the user is a student.
     *
     * @return true if the user is a student, false otherwise.
     */
    suspend fun isStudent(): Boolean

    /**
     * Saves the student class.
     *
     * @param name the name of the class.
     */
    suspend fun saveClass(name: String)

    /**
     * Gets the student class.
     *
     * @return the name of the class.
     */
    suspend fun getUserClass(): String

    /**
     * Saves the school year.
     *
     * @param year the year to save.
     */
    suspend fun saveYear(year: String)

    /**
     * Gets the school year.
     *
     * @return the year.
     */
    suspend fun getYear(): String

    /**
     * Gets all the classes from the database.
     *
     * @return the classes.
     */
    suspend fun getClasses(): List<SchoolClass>

    /**
     * Gets all the professors from the database.
     *
     * @return the professors.
     */
    suspend fun getProfessors(): List<Professor>

    /**
     * Gets all the teaches from the database.
     *
     * @return the teaches.
     */
    suspend fun getTeaches(): List<Teach>
}
