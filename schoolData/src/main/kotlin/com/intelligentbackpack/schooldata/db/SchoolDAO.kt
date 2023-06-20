package com.intelligentbackpack.schooldata.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.intelligentbackpack.schooldata.db.entities.Lesson
import com.intelligentbackpack.schooldata.db.entities.Professor
import com.intelligentbackpack.schooldata.db.entities.SchoolClass
import com.intelligentbackpack.schooldata.db.entities.Subject
import com.intelligentbackpack.schooldata.db.entities.Teach
import java.time.LocalDate
import java.time.LocalTime

/**
 * School DAO to access database
 */
@Dao
internal interface SchoolDAO {

    /**
     * Insert a subject
     *
     * @param subject the subject to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubject(subject: Subject)

    /**
     * Insert a lesson
     *
     * @param lesson the lesson to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLesson(lesson: Lesson)

    /**
     * Insert a professor
     *
     * @param professor the professor to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProfessor(professor: Professor)

    /**
     * Insert a class
     *
     * @param schoolClass the class to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertClass(schoolClass: SchoolClass)

    /**
     * Insert a teach
     *
     * @param teach the teach to insert
     * @return the id of the inserted teach
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTeach(teach: Teach): Long

    /**
     * Delete all lessons
     */
    @Query("DELETE FROM Lessons")
    fun deleteLessons()

    /**
     * Delete all subjects
     */
    @Query("DELETE FROM Subjects")
    fun deleteSubjects()

    /**
     * Delete all professors
     */
    @Query("DELETE FROM Professors")
    fun deleteProfessors()

    /**
     * Delete all classes
     */
    @Query("DELETE FROM Classes")
    fun deleteClasses()

    /**
     * Delete all teaches
     */
    @Query("DELETE FROM Teaches")
    fun deleteTeaches()

    /**
     * Get all subjects
     *
     * @return all subjects
     */
    @Query("SELECT * FROM Subjects")
    fun getSubjects(): List<Subject>

    /**
     * Get a lessons by day, start time, end time, from date and to date
     *
     * @param day the day of the lesson
     * @param startTime the start time of the lesson
     * @param endTime the end time of the lesson
     * @param fromDate the from date of the lesson
     * @param toDate the to date of the lesson
     * @return the lesson if found, null otherwise
     *
     */
    @Query(
        "SELECT * " +
            "FROM Lessons " +
            "WHERE day = :day " +
            "AND start_time = :startTime " +
            "AND end_time = :endTime " +
            "AND from_date = :fromDate " +
            "AND to_date = :toDate",
    )
    fun getLesson(day: Int, startTime: LocalTime, endTime: LocalTime, fromDate: LocalDate, toDate: LocalDate): Lesson?

    /**
     * Get all lessons
     *
     * @return all lessons
     */
    @Query("SELECT * FROM Lessons")
    fun getLessons(): List<Lesson>

    /**
     * Get all classes
     *
     * @return all classes
     */
    @Query("SELECT * FROM Classes")
    fun getClasses(): List<SchoolClass>

    /**
     * Get all professors
     *
     * @return all professors
     */
    @Query("SELECT * FROM Professors")
    fun getProfessors(): List<Professor>

    /**
     * Get all teaches
     *
     * @return all teaches
     */
    @Query("SELECT * FROM Teaches")
    fun getTeaches(): List<Teach>
}
