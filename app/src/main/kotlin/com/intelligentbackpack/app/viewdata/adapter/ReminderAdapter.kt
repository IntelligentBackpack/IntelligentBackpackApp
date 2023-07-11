package com.intelligentbackpack.app.viewdata.adapter

import com.intelligentbackpack.app.viewdata.EventView
import com.intelligentbackpack.app.viewdata.ReminderView
import com.intelligentbackpack.app.viewdata.adapter.EventAdapter.fromViewToDomain
import com.intelligentbackpack.app.viewdata.adapter.SchoolSupplyAdapter.fromDomainToView
import com.intelligentbackpack.reminderdomain.adapter.EventAdapter
import com.intelligentbackpack.reminderdomain.adapter.EventAdapter.fromSchoolToReminder
import com.intelligentbackpack.reminderdomain.adapter.ReminderWithSupply
import com.intelligentbackpack.reminderdomain.entitites.ReminderForLesson
import com.intelligentbackpack.reminderdomain.entitites.ReminderForLessonDate
import com.intelligentbackpack.reminderdomain.entitites.ReminderForLessonIntervalPeriod
import java.time.LocalDate

/**
 * Adapter for the reminder view.
 */
object ReminderAdapter {

    /**
     * Converts a reminder from the domain to the view.
     *
     * @param event the event for the reminder.
     * @return the reminder for the view.
     */
    fun ReminderWithSupply.fromDomainToView(event: EventView): ReminderView {
        val reminder = this.reminder
        return ReminderView(
            schoolSupplyView = this.supply.fromDomainToView(),
            eventView = event,
            fromDate = when (reminder) {
                is ReminderForLessonIntervalPeriod -> reminder.startDate.toString()
                else -> null
            },
            toDate = when (reminder) {
                is ReminderForLessonIntervalPeriod -> reminder.endDate.toString()
                else -> null
            },
            date = when (reminder) {
                is ReminderForLessonDate -> reminder.date.toString()
                else -> null
            },
        )
    }

    /**
     * Converts a reminder from the view to the domain.
     *
     * @return the reminder for the domain.
     */
    fun ReminderView.fromViewToDomain(): ReminderForLesson {
        val book = schoolSupplyView.book
        return if (book != null) {
            when (val event = eventView.fromViewToDomain()?.fromSchoolToReminder()) {
                is EventAdapter.Lesson ->
                    when {
                        date != null -> ReminderForLessonDate.create(
                            isbn = book.isbn,
                            lesson = event,
                            date = LocalDate.parse(date),
                        )

                        fromDate != null && toDate != null -> ReminderForLessonIntervalPeriod.create(
                            isbn = book.isbn,
                            lesson = event,
                            startDate = LocalDate.parse(fromDate),
                            endDate = LocalDate.parse(toDate),
                        )

                        else -> throw IllegalArgumentException("Invalid reminder")
                    }

                else -> throw IllegalArgumentException("Invalid reminder")
            }
        } else {
            throw IllegalArgumentException("Invalid reminder")
        }
    }
}
