package com.intelligentbackpack.schooldomain.entities.calendar.alteration

import com.intelligentbackpack.schooldomain.entities.calendar.CalendarEvent
import com.intelligentbackpack.schooldomain.entities.calendar.DateEvent
import com.intelligentbackpack.schooldomain.entities.calendar.WeekEvent
import com.intelligentbackpack.schooldomain.exceptions.EventDateException
import java.time.LocalDate

/**
 * Factory for creating alteration of events.
 *
 */
object AlterationFactory {
    private fun isEventOnDate(event: CalendarEvent, date: LocalDate): Boolean =
        when (event) {
            is DateEvent -> event.date == date
            is WeekEvent ->
                event.day == date.dayOfWeek &&
                    event.fromDate <= date && event.toDate >= date

            else -> false
        }

    /**
     * Creates an alteration for a new event.
     *
     * @param newEvent the new event
     * @return the alteration for the new event
     */
    fun createNewEvent(newEvent: CalendarEvent): AlterationEvent =
        object : NewEvent {
            override fun isNewEventOnDate(date: LocalDate): Boolean = isEventOnDate(newEvent, date)

            override val event: CalendarEvent
                get() = newEvent
        }

    private fun checkCancelOriginalEventSingleDay(
        originalEvent: CalendarEvent,
        date: LocalDate,
        alterationEvent: AlterationEvent,
    ): AlterationEvent {
        when (originalEvent) {
            is DateEvent -> {
                if (originalEvent.date != date) {
                    throw EventDateException()
                } else {
                    return alterationEvent
                }
            }

            is WeekEvent -> {
                if (originalEvent.day != date.dayOfWeek) {
                    throw EventDateException()
                } else if (originalEvent.fromDate.isAfter(date) || originalEvent.toDate.isBefore(date)) {
                    throw EventDateException()
                } else {
                    return alterationEvent
                }
            }

            else -> {
                throw IllegalArgumentException("Original event must be a DateEvent or WeekEvent")
            }
        }
    }

    private fun checkCancelOriginalEventIntervalOfDays(
        originalEvent: CalendarEvent,
        initialDate: LocalDate,
        finalDate: LocalDate,
        alterationEvent: AlterationEvent,
    ): AlterationEvent {
        if (initialDate.isAfter(finalDate)) {
            throw EventDateException()
        } else {
            when (originalEvent) {
                is DateEvent -> {
                    if (originalEvent.date !in initialDate..finalDate) {
                        throw EventDateException()
                    } else {
                        return alterationEvent
                    }
                }

                is WeekEvent -> {
                    if (originalEvent.fromDate.isAfter(initialDate) || originalEvent.toDate.isBefore(finalDate)) {
                        throw EventDateException()
                    } else {
                        return alterationEvent
                    }
                }

                else -> {
                    throw IllegalArgumentException("Original event must be a DateEvent or WeekEvent")
                }
            }
        }
    }

    private fun checkRescheduleEvent(
        originalEvent: CalendarEvent,
        newEvent: CalendarEvent,
        checkCancelOriginalEvent: () -> AlterationEvent,
    ): AlterationEvent {
        check(newEvent != originalEvent)
        return checkCancelOriginalEvent()
    }

    /**
     * Creates an alteration for a rescheduled event that occurs in a single day.
     *
     * @param newEvent the new event
     * @param dateOriginalEvent the date of the original event
     * @param originalEvent the original event
     * @return the alteration for the rescheduled event
     * @throws IllegalArgumentException if the new event is the same as the original event
     * @throws EventDateException if the date of the original event is different from the date of the new event
     * @throws IllegalArgumentException if the original event is not a [DateEvent] or [WeekEvent]
     */
    fun createRescheduleSingleDayEvent(
        newEvent: CalendarEvent,
        dateOriginalEvent: LocalDate,
        originalEvent: CalendarEvent,
    ): AlterationEvent {
        val alterationEvent = object : RescheduleEvent, SingleDayAlteration {
            override val event: CalendarEvent
                get() = newEvent
            override val date: LocalDate
                get() = dateOriginalEvent
            override val originalEvent: CalendarEvent
                get() = originalEvent

            override fun wasEventRescheduledOnDate(date: LocalDate): Boolean = this.date == date

            override fun isNewEventOnDate(date: LocalDate): Boolean = isEventOnDate(newEvent, date)
        }
        return checkRescheduleEvent(originalEvent, newEvent) {
            checkCancelOriginalEventSingleDay(originalEvent, dateOriginalEvent, alterationEvent)
        }
    }

    /**
     * Creates an alteration for a rescheduled event that occurs in an interval of days.
     *
     * @param initialDate the initial date of the interval
     * @param finalDate the final date of the interval
     * @param originalEvent the original event
     * @param newEvent the new event
     * @return the alteration for the rescheduled event
     * @throws IllegalArgumentException if the new event is the same as the original event
     * @throws EventDateException if the interval of the original event is different from the interval of the new event
     * @throws IllegalArgumentException if the original event is not a [DateEvent] or [WeekEvent]
     */
    fun createRescheduleIntervalOfDaysEvent(
        initialDate: LocalDate,
        finalDate: LocalDate,
        originalEvent: CalendarEvent,
        newEvent: CalendarEvent,
    ): AlterationEvent {
        val alterationEvent = object : RescheduleEvent, IntervalOfDaysAlteration {
            override val event: CalendarEvent
                get() = newEvent
            override val initialDate: LocalDate
                get() = initialDate
            override val finalDate: LocalDate
                get() = finalDate
            override val originalEvent: CalendarEvent
                get() = originalEvent

            override fun wasEventRescheduledOnDate(date: LocalDate): Boolean =
                date in initialDate..finalDate

            override fun isNewEventOnDate(date: LocalDate): Boolean = isEventOnDate(newEvent, date)
        }
        return checkRescheduleEvent(originalEvent, newEvent) {
            checkCancelOriginalEventIntervalOfDays(originalEvent, initialDate, finalDate, alterationEvent)
        }
    }

    /**
     * Creates an alteration for a cancelled event that occurs in a single day.
     *
     * @param date the date of the cancelled event
     * @param cancelledEvent the cancelled event
     * @return the alteration for the cancelled event
     * @throws EventDateException if the date of the cancelled event is different from the date of the new event
     * @throws IllegalArgumentException if the cancelled event is not a [DateEvent] or [WeekEvent]
     */
    fun createCancelSingleDayEvent(date: LocalDate, cancelledEvent: CalendarEvent): AlterationEvent {
        val alterationEvent = object : CancelEvent, SingleDayAlteration {
            override fun isEventCancelledOnDate(date: LocalDate): Boolean =
                date == this.date

            override val event: CalendarEvent
                get() = cancelledEvent
            override val date: LocalDate
                get() = date
        }
        return checkCancelOriginalEventSingleDay(cancelledEvent, date, alterationEvent)
    }

    /**
     * Creates an alteration for a cancelled event that occurs in an interval of days.
     *
     * @param initialDate the initial date of the interval
     * @param finalDate the final date of the interval
     * @param cancelledEvent the cancelled event
     * @return the alteration for the cancelled event
     * @throws EventDateException if the interval of the cancelled event is different from the interval of the new event
     * @throws IllegalArgumentException if the cancelled event is not a [DateEvent] or [WeekEvent]
     */
    fun createCancelIntervalOfDaysEvent(
        initialDate: LocalDate,
        finalDate: LocalDate,
        cancelledEvent: CalendarEvent,
    ): AlterationEvent {
        val alterationEvent = object : CancelEvent, IntervalOfDaysAlteration {
            override fun isEventCancelledOnDate(date: LocalDate): Boolean {
                return date in initialDate..finalDate
            }

            override val event: CalendarEvent
                get() = cancelledEvent
            override val initialDate: LocalDate
                get() = initialDate
            override val finalDate: LocalDate
                get() = finalDate
        }
        return checkCancelOriginalEventIntervalOfDays(cancelledEvent, initialDate, finalDate, alterationEvent)
    }
}
