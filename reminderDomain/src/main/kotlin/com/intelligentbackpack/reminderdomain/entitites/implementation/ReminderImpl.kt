package com.intelligentbackpack.reminderdomain.entitites.implementation

import com.intelligentbackpack.reminderdomain.adapter.EventAdapter
import com.intelligentbackpack.reminderdomain.entitites.Reminder
import com.intelligentbackpack.reminderdomain.entitites.ReminderForLesson
import java.time.LocalDate

/**
 * Reminder implementation
 *
 * @param booksForLesson map of books for lesson
 * @param lessonsForBook map of lessons for book
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
        require(oldReminderForLesson.lesson in booksForLesson) { "Lesson ${oldReminderForLesson.lesson} not found" }
        require(oldReminderForLesson in (booksForLesson[oldReminderForLesson.lesson] ?: setOf())) {
            "Book ${oldReminderForLesson.isbn} not found in lesson"
        }
        require(oldReminderForLesson.lesson == newReminderForLesson.lesson) {
            "Lessons ${oldReminderForLesson.lesson} and ${newReminderForLesson.lesson} are not equal"
        }
        require(oldReminderForLesson.isbn == newReminderForLesson.isbn) {
            "Books ${oldReminderForLesson.isbn} and ${newReminderForLesson.isbn} are not equal"
        }
        return if (oldReminderForLesson == newReminderForLesson) {
            this
        } else {
            ReminderImpl(
                booksForLesson = booksForLesson + (
                    oldReminderForLesson.lesson to (
                        booksForLesson[oldReminderForLesson.lesson]
                            ?.filter { it != oldReminderForLesson }
                            ?.toSet()
                            ?: setOf()
                        ) + newReminderForLesson
                    ),
                lessonsForBook = lessonsForBook + (
                    oldReminderForLesson.isbn to (
                        lessonsForBook[oldReminderForLesson.isbn]
                            ?.filter { it != oldReminderForLesson }
                            ?.toSet()
                            ?: setOf()
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
        require(reminderForLesson.lesson in booksForLesson) { "Lesson ${reminderForLesson.lesson} not found" }
        require(reminderForLesson in (booksForLesson[reminderForLesson.lesson] ?: setOf())) {
            "Book ${reminderForLesson.isbn} not found in lesson"
        }
        return ReminderImpl(
            booksForLesson = booksForLesson + (
                reminderForLesson.lesson to (
                    booksForLesson[reminderForLesson.lesson]
                        ?.filter { it != reminderForLesson }
                        ?.toSet()
                        ?: setOf()
                    )
                ),
            lessonsForBook = lessonsForBook + (
                reminderForLesson.isbn to (
                    lessonsForBook[reminderForLesson.isbn]
                        ?.filter { it != reminderForLesson }
                        ?.toSet()
                        ?: setOf()
                    )
                ),
        )
    }

    override fun getBooksForLesson(event: EventAdapter.CalendarEvent): Set<ReminderForLesson> {
        return booksForLesson[event]?.toSet() ?: setOf()
    }

    override fun getLessonsForBook(isbn: String): Set<ReminderForLesson> {
        return lessonsForBook[isbn]?.toSet() ?: setOf()
    }

    override fun getBooksForLessonInDate(event: EventAdapter.CalendarEvent, date: LocalDate): Set<String> {
        return booksForLesson[event]
            ?.filter { it.isInInterval(date) }
            ?.map { it.isbn }
            ?.toSet() ?: setOf()
    }
}
