package com.intelligentbackpack.reminderdata.datasource

import com.intelligentbackpack.reminderdata.db.entities.Lesson
import com.intelligentbackpack.reminderdata.db.entities.Reminder
import com.intelligentbackpack.reminderdata.db.entities.Subject
import java.time.LocalDate
import java.time.LocalTime

interface ReminderLocalDataSource {

    suspend fun getSubjects(): List<Subject>

    suspend fun insertSubject(subject: Subject)

    suspend fun insertSubjects(subjects: List<Subject>) {
        subjects.forEach { insertSubject(it) }
    }

    suspend fun saveYear(year: String)

    suspend fun getYear(): String

    suspend fun saveCalendarId(calendarId: Int)

    suspend fun getCalendarId(): Int

    suspend fun saveLesson(lesson: Lesson)

    suspend fun saveLessons(lessons: List<Lesson>) {
        lessons.forEach { saveLesson(it) }
    }

    suspend fun getLesson(
        day: Int,
        startTime: LocalTime,
        endTime: LocalTime,
        fromDate: LocalDate,
        toDate: LocalDate,
    ): Lesson?

    suspend fun getLessons(): List<Lesson>

    suspend fun saveReminder(reminder: Reminder)

    suspend fun saveReminders(reminders: List<Reminder>) {
        reminders.forEach { saveReminder(it) }
    }

    suspend fun getReminders(): List<Reminder>

    suspend fun deleteData()
}
