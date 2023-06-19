package com.intelligentbackpack.reminderdata.datasource

import com.intelligentbackpack.reminderdata.db.ReminderDatabase
import com.intelligentbackpack.reminderdata.db.entities.Lesson
import com.intelligentbackpack.reminderdata.db.entities.Reminder
import com.intelligentbackpack.reminderdata.db.entities.Subject
import java.time.LocalDate
import java.time.LocalTime

/**
 * Local data source for the reminder module.
 */
class ReminderLocalDataSourceImpl(
    private val database: ReminderDatabase,
) : ReminderLocalDataSource {
    override suspend fun getSubjects(): List<Subject> {
        return database.reminderDao().getSubjects()
    }

    override suspend fun insertSubject(subject: Subject) {
        database.reminderDao().insertSubject(subject)
    }

    override suspend fun saveLesson(lesson: Lesson) {
        database.reminderDao().insertLesson(lesson)
    }

    override suspend fun getLesson(
        day: Int,
        startTime: LocalTime,
        endTime: LocalTime,
        fromDate: LocalDate,
        toDate: LocalDate,
    ): Lesson? {
        return database.reminderDao().getLesson(
            day,
            startTime,
            endTime,
            fromDate,
            toDate,
        )
    }

    override suspend fun getLessons(): List<Lesson> {
        return database.reminderDao().getLessons()
    }

    override suspend fun saveReminder(reminder: Reminder) {
        database.reminderDao().insertReminder(reminder)
    }

    override suspend fun getReminders(): List<Reminder> {
        return database.reminderDao().getReminders()
    }

    override suspend fun deleteData() {
        database.reminderDao().deleteReminders()
        database.reminderDao().deleteLessons()
    }
}
