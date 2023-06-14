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
 * Desktop DAO to access database
 */
@Dao
internal interface ReminderDAO {

    /**
     * Insert a subject
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubject(subject: Subject)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLesson(lesson: Lesson)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReminder(reminders: Reminder)

    @Query("DELETE FROM Subjects")
    fun deleteReminders()

    @Query("DELETE FROM Lessons")
    fun deleteLessons()

    /**
     * Get all subjects
     */
    @Query("SELECT * FROM Subjects")
    fun getSubjects(): List<Subject>

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

    @Query(
        "SELECT * " +
            "FROM Reminders " +
            "WHERE lesson_id = :lessonId " +
            "AND isbn = :isbn " +
            "AND from_date = :fromDate " +
            "AND to_date = :toDate",
    )
    fun getReminder(lessonId: Int, isbn: String, fromDate: String, toDate: String): Reminder?

    @Query("SELECT * FROM Lessons")
    fun getLessons(): List<Lesson>

    @Query("SELECT * FROM Reminders")
    fun getReminders(): List<Reminder>
}
