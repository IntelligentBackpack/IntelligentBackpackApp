package com.intelligentbackpack.reminderdata.adapter

import com.intelligentbackpack.reminderdata.adapter.LessonAdapter.fromDBToDomain
import com.intelligentbackpack.reminderdata.db.entities.Subject
import com.intelligentbackpack.reminderdomain.entitites.ReminderForLesson
import com.intelligentbackpack.reminderdomain.entitites.ReminderForLessonDate
import com.intelligentbackpack.reminderdomain.entitites.ReminderForLessonIntervalPeriod
import com.intelligentbackpack.reminderdata.db.entities.Lesson as DBLesson
import com.intelligentbackpack.reminderdata.db.entities.Reminder as DBReminder

/**
 * Adapter for converting [DBReminder] to [ReminderForLesson] and vice versa or [ReminderForLesson] to [DBReminder] and vice versa.
 */
object ReminderAdapter {

    /**
     * Converts a [String] to a [DBReminder].
     *
     * @param lesson The [DBLesson] to get the [DBLesson.fromDate] and [DBLesson.toDate] from.
     */
    fun String.fromRemoteToDB(lesson: DBLesson): DBReminder {
        return DBReminder(
            id = 0,
            lessonId = lesson.id,
            isbn = this@fromRemoteToDB,
            fromDate = lesson.fromDate,
            toDate = lesson.toDate,
        )
    }

    /**
     * Converts a [DBReminder] to a [ReminderForLesson].
     * if the [DBReminder] is a [DBReminder] with the same [DBReminder.fromDate] and [DBReminder.toDate] then it will be converted to a [ReminderForLessonDate].
     * if the [DBReminder] is a [DBReminder] with different [DBReminder.fromDate] and [DBReminder.toDate] then it will be converted to a [ReminderForLessonIntervalPeriod].
     *
     * @param lessons The list of lessons to get the lesson from the lesson id.
     */
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

    /**
     * Converts a [ReminderForLesson] to a [DBReminder].
     *
     * @param lesson The [DBLesson] to get the [DBLesson.id] from.
     */
    fun ReminderForLesson.fromDomainToDB(lesson: DBLesson): DBReminder {
        return DBReminder(
            id = 0,
            lessonId = lesson.id,
            isbn = isbn,
            fromDate = when (this) {
                is ReminderForLessonDate -> date
                is ReminderForLessonIntervalPeriod -> startDate
                else -> throw IllegalStateException("Unknown type of reminder")
            },
            toDate = when (this) {
                is ReminderForLessonDate -> date
                is ReminderForLessonIntervalPeriod -> endDate
                else -> throw IllegalStateException("Unknown type of reminder")
            },
        )
    }
}
