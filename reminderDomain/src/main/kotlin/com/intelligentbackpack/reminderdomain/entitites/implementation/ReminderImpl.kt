package com.intelligentbackpack.reminderdomain.entitites.implementation

import com.intelligentbackpack.reminderdomain.adapter.EventAdapter
import com.intelligentbackpack.reminderdomain.entitites.Reminder
import com.intelligentbackpack.reminderdomain.entitites.ReminderForLesson
import java.time.LocalDate

/**
 * Reminder implementation
 */
data class ReminderImpl(
    val booksForLesson: Map<EventAdapter.CalendarEvent, Set<ReminderForLesson>> = mapOf(),
    val lessonsForBook: Map<String, Set<ReminderForLesson>> = mapOf(),
) : Reminder {
    override fun addBookForLesson(reminderForLesson: ReminderForLesson): Reminder =
        ReminderImpl(
            booksForLesson = booksForLesson + (
                reminderForLesson.lesson to (
                    booksForLesson[reminderForLesson.lesson]
                        ?: setOf()
                    ) + reminderForLesson
                ),
            lessonsForBook = lessonsForBook + (
                reminderForLesson.isbn to (
                    lessonsForBook[reminderForLesson.isbn]
                        ?: setOf()
                    ) + reminderForLesson
                ),
        )

    /**
     * Change period of book for lesson
     *
     * @param oldReminderForLesson old reminder for lesson
     * @param newReminderForLesson new reminder for lesson
     * @return new reminder
     * @throws IllegalArgumentException if oldReminderForLesson not found in reminder
     * @throws IllegalArgumentException if oldReminderForLesson.lesson is equal to newReminderForLesson.lesson
     * @throws IllegalArgumentException if oldReminderForLesson.isbn is equal to newReminderForLesson.isbn
     */
    override fun changePeriodOfBookForLesson(
        oldReminderForLesson: ReminderForLesson,
        newReminderForLesson: ReminderForLesson,
    ): Reminder {
        if (oldReminderForLesson.lesson !in booksForLesson) {
            throw IllegalArgumentException("Lesson ${oldReminderForLesson.lesson} not found")
        } else if (oldReminderForLesson !in booksForLesson[oldReminderForLesson.lesson]!!) {
            throw IllegalArgumentException("Book ${oldReminderForLesson.isbn} not found in lesson")
        } else if (oldReminderForLesson.lesson != newReminderForLesson.lesson) {
            throw IllegalArgumentException(
                "Lessons ${oldReminderForLesson.lesson} and ${newReminderForLesson.lesson} are not equal",
            )
        } else if (oldReminderForLesson.isbn != newReminderForLesson.isbn) {
            throw IllegalArgumentException(
                "Books ${oldReminderForLesson.isbn} and ${newReminderForLesson.isbn} are not equal",
            )
        } else if (oldReminderForLesson == newReminderForLesson) {
            return this
        } else {
            return ReminderImpl(
                booksForLesson = booksForLesson + (
                    oldReminderForLesson.lesson to (
                        booksForLesson[oldReminderForLesson.lesson]!!
                            .filter { it != oldReminderForLesson }
                            .toSet()
                        ) + newReminderForLesson
                    ),
                lessonsForBook = lessonsForBook + (
                    oldReminderForLesson.isbn to (
                        lessonsForBook[oldReminderForLesson.isbn]!!
                            .filter { it != oldReminderForLesson }
                            .toSet()
                        ) + newReminderForLesson
                    ),
            )
        }
    }

    /**
     * Remove book for lesson
     *
     * @param reminderForLesson reminder for lesson
     * @return reminder
     * @throws IllegalArgumentException if lesson or book not found
     * @throws IllegalArgumentException if book not found
     */
    override fun removeBookForLesson(reminderForLesson: ReminderForLesson): Reminder {
        if (reminderForLesson.lesson !in booksForLesson) {
            throw IllegalArgumentException("Lesson ${reminderForLesson.lesson} not found")
        } else if (reminderForLesson !in booksForLesson[reminderForLesson.lesson]!!) {
            throw IllegalArgumentException("Book ${reminderForLesson.isbn} not found")
        } else {
            return ReminderImpl(
                booksForLesson = booksForLesson + (
                    reminderForLesson.lesson to (
                        booksForLesson[reminderForLesson.lesson]!!
                            .filter { it != reminderForLesson }
                            .toSet()
                        )
                    ),
                lessonsForBook = lessonsForBook + (
                    reminderForLesson.isbn to (
                        lessonsForBook[reminderForLesson.isbn]!!
                            .filter { it != reminderForLesson }
                            .toSet()
                        )
                    ),
            )
        }
    }

    override fun getBooksForLesson(event: EventAdapter.CalendarEvent): Set<String> {
        return booksForLesson[event]?.map { it.isbn }?.toSet() ?: setOf()
    }

    override fun getLessonsForBook(isbn: String): Set<EventAdapter.Lesson> {
        return lessonsForBook[isbn]?.map { it.lesson }?.toSet() ?: setOf()
    }

    override fun getBooksForLessonInDate(event: EventAdapter.CalendarEvent, date: LocalDate): Set<String> {
        return booksForLesson[event]
            ?.filter { it.isInInterval(date) }
            ?.map { it.isbn }
            ?.toSet() ?: setOf()
    }
}
