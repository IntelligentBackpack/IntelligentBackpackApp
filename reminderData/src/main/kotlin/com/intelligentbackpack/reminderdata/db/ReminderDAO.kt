package com.intelligentbackpack.reminderdata.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.intelligentbackpack.reminderdata.db.entities.Lesson
import com.intelligentbackpack.reminderdata.db.entities.Reminder
import com.intelligentbackpack.reminderdata.db.entities.Subject
import java.time.LocalDate
import java.time.LocalTime

/**
 * Reminder DAO to access database
 */
@Dao
internal interface ReminderDAO {

    /**
     * Insert a subject
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubject(subject: Subject)

    /**
     * Insert a lesson
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLesson(lesson: Lesson)

    /**
     * Insert a reminder
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReminder(reminders: Reminder)

    /**
     * Delete all subjects
     */
    @Query("DELETE FROM Subjects")
    fun deleteReminders()

    /**
     * Delete all lessons
     */
    @Query("DELETE FROM Lessons")
    fun deleteLessons()

    /**
     * Get all subjects
     */
    @Query("SELECT * FROM Subjects")
    fun getSubjects(): List<Subject>

    /**
     * Get the lesson with the given parameters
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
     * Get the reminder with the given parameters
     */
    @Query(
        "SELECT * " +
            "FROM Reminders " +
            "WHERE lesson_id = :lessonId " +
            "AND isbn = :isbn " +
            "AND from_date = :fromDate " +
            "AND to_date = :toDate",
    )
    fun getReminder(lessonId: Int, isbn: String, fromDate: LocalDate, toDate: LocalDate): Reminder?

    /**
     * Get all lessons
     */
    @Query("SELECT * FROM Lessons")
    fun getLessons(): List<Lesson>

    /**
     * Get all reminders
     */
    @Query("SELECT * FROM Reminders")
    fun getReminders(): List<Reminder>

    /**
     * Delete a reminder
     */
    @Query("DELETE FROM Reminders WHERE id = :id")
    fun deleteReminder(id: Int)
}
