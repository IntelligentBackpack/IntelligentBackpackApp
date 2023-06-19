package com.intelligentbackpack.reminderdata.datasource

import com.intelligentbackpack.reminderdata.db.entities.Lesson
import com.intelligentbackpack.reminderdata.db.entities.Reminder
import com.intelligentbackpack.reminderdata.db.entities.Subject
import java.time.LocalDate
import java.time.LocalTime

/**
 * Local data source for the reminder module.
 */
interface ReminderLocalDataSource {

    /**
     * Gets all the subjects.
     *
     * @return the subjects.
     */
    suspend fun getSubjects(): List<Subject>

    /**
     * Inserts a subject.
     *
     * @param subject the subject.
     */
    suspend fun insertSubject(subject: Subject)

    /**
     * Inserts the subjects.
     *
     * @param subjects the subjects.
     */
    suspend fun insertSubjects(subjects: List<Subject>) {
        subjects.forEach { insertSubject(it) }
    }

    /**
     * Saves a lesson.
     *
     * @param lesson the lesson.
     */
    suspend fun saveLesson(lesson: Lesson)

    /**
     * Saves the lessons.
     *
     * @param lessons the lessons.
     */
    suspend fun saveLessons(lessons: List<Lesson>) {
        lessons.forEach { saveLesson(it) }
    }

    /**
     * Gets the lesson for the given parameters.
     *
     * @param day the day.
     * @param startTime the start time.
     * @param endTime the end time.
     * @param fromDate the from date.
     * @param toDate the to date.
     * @return the lesson.
     */
    suspend fun getLesson(
        day: Int,
        startTime: LocalTime,
        endTime: LocalTime,
        fromDate: LocalDate,
        toDate: LocalDate,
    ): Lesson?

    /**
     * Gets all the lessons.
     *
     * @return the lessons.
     */
    suspend fun getLessons(): List<Lesson>

    /**
     * Saves a reminder.
     *
     * @param reminder the reminder.
     */
    suspend fun saveReminder(reminder: Reminder)

    /**
     * Saves the reminders.
     *
     * @param reminders the reminders.
     */
    suspend fun saveReminders(reminders: List<Reminder>) {
        reminders.forEach { saveReminder(it) }
    }

    /**
     * Gets all the reminders.
     *
     * @return the reminders.
     */
    suspend fun getReminders(): List<Reminder>

    /**
     * Deletes all the data.
     */
    suspend fun deleteData()
}
