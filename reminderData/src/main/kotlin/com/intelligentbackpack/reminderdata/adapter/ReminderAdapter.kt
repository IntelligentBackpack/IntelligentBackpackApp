package com.intelligentbackpack.reminderdata.adapter

import com.intelligentbackpack.reminderdata.adapter.LessonAdapter.fromDBToDomain
import com.intelligentbackpack.reminderdata.db.entities.Subject
import com.intelligentbackpack.reminderdomain.entitites.ReminderForLesson
import com.intelligentbackpack.reminderdomain.entitites.ReminderForLessonDate
import com.intelligentbackpack.reminderdomain.entitites.ReminderForLessonIntervalPeriod
import com.intelligentbackpack.reminderdata.db.entities.Lesson as DBLesson
import com.intelligentbackpack.reminderdata.db.entities.Reminder as DBReminder

object ReminderAdapter {

    fun String.fromRemoteToDB(lesson: DBLesson): DBReminder {
        return DBReminder(
            id = 0,
            lessonId = lesson.id,
            isbn = this@fromRemoteToDB,
            fromDate = lesson.fromDate,
            toDate = lesson.toDate,
        )
    }

    fun DBReminder.fromDBToDomain(lessons: List<DBLesson>, subjects: List<Subject>): ReminderForLesson {
        if (fromDate == toDate) {
            return ReminderForLessonDate.create(
                isbn = isbn,
                date = fromDate,
                lesson = lessons.first { it.id == lessonId }.fromDBToDomain(subjects),
            )
        }
        return ReminderForLessonIntervalPeriod.create(
            isbn = isbn,
            startDate = fromDate,
            endDate = toDate,
            lesson = lessons.first { it.id == lessonId }.fromDBToDomain(subjects),
        )
    }
}
