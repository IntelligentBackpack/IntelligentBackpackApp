package com.intelligentbackpack.schooldomain.policy

import com.intelligentbackpack.schooldomain.entities.Class
import com.intelligentbackpack.schooldomain.entities.calendar.CalendarEvent
import com.intelligentbackpack.schooldomain.entities.calendar.DateLesson
import com.intelligentbackpack.schooldomain.entities.calendar.Lesson
import com.intelligentbackpack.schooldomain.entities.calendar.WeekLesson
import com.intelligentbackpack.schooldomain.entities.calendar.alteration.AlterationEvent
import com.intelligentbackpack.schooldomain.entities.calendar.alteration.CancelEvent
import com.intelligentbackpack.schooldomain.entities.calendar.alteration.IntervalOfDaysAlteration
import com.intelligentbackpack.schooldomain.entities.calendar.alteration.NewEvent
import com.intelligentbackpack.schooldomain.entities.calendar.alteration.RescheduleEvent
import com.intelligentbackpack.schooldomain.entities.calendar.alteration.SingleDayAlteration
import com.intelligentbackpack.schooldomain.entities.person.Professor
import com.intelligentbackpack.schooldomain.policy.CalendarPolicy.isOverlappingDate
import com.intelligentbackpack.schooldomain.policy.CalendarPolicy.isOverlappingTime
import java.time.LocalDate

/**
 * A policy that checks if an alteration is valid.
 */
object AlterationPolicy {

    private fun isDateInInterval(
        date: LocalDate,
        initialDate: LocalDate,
        finalDate: LocalDate,
    ): Boolean {
        return (initialDate..finalDate).contains(date)
    }

    private fun isNewLessonOverlapping(newLesson: WeekLesson, lesson: CalendarEvent) =
        when (lesson) {
            is WeekLesson -> lesson.day == newLesson.day && isOverlappingDate(newLesson, lesson)
            is DateLesson -> isDateInInterval(lesson.date, newLesson.fromDate, newLesson.toDate)
            else -> false
        }

    private fun isNewLessonOverlapping(newLesson: DateLesson, lesson: CalendarEvent) =
        when (lesson) {
            is WeekLesson -> lesson.day == newLesson.date.dayOfWeek && isOverlappingDate(lesson, newLesson)
            is DateLesson -> isOverlappingDate(lesson, newLesson)
            else -> false
        }

    private fun isInIntersectionOfInterval(event: CalendarEvent, alterationEvent: AlterationEvent) =
        when (event) {
            is WeekLesson -> {
                when (alterationEvent) {
                    is SingleDayAlteration -> isDateInInterval(
                        event.fromDate,
                        alterationEvent.date,
                        alterationEvent.date,
                    ) ||
                        isDateInInterval(event.toDate, alterationEvent.date, alterationEvent.date)

                    is IntervalOfDaysAlteration -> isDateInInterval(
                        event.fromDate,
                        alterationEvent.initialDate,
                        alterationEvent.finalDate,
                    ) ||
                        isDateInInterval(event.toDate, alterationEvent.initialDate, alterationEvent.finalDate)

                    else -> false
                }
            }

            is DateLesson -> {
                when (alterationEvent) {
                    is SingleDayAlteration -> event.date == alterationEvent.date
                    is IntervalOfDaysAlteration -> isDateInInterval(
                        event.date,
                        alterationEvent.initialDate,
                        alterationEvent.finalDate,
                    )

                    else -> false
                }
            }

            else -> false
        }

    private fun areEventsInTimeTableOverlapping(
        newLesson: CalendarEvent,
        lessons: Set<CalendarEvent>,
        newEvents: Set<NewEvent>,
        rescheduleEvents: Set<RescheduleEvent>,
        cancelEvents: Set<CancelEvent>,
    ): Boolean {
        val allLesson = (
            lessons +
                newEvents.mapNotNull { it.event as Lesson? } +
                rescheduleEvents.mapNotNull { it.event as Lesson? }
            ).filter { lesson ->
            cancelEvents.none {
                it.event == lesson &&
                    isInIntersectionOfInterval(newLesson, it)
            }
        }
            .filter { lesson ->
                rescheduleEvents.none {
                    it.originalEvent == lesson &&
                        isInIntersectionOfInterval(newLesson, it)
                }
            }
        return when (newLesson) {
            is WeekLesson ->
                checkOverlapping(newLesson, allLesson) {
                    isNewLessonOverlapping(newLesson, it)
                }

            is DateLesson ->
                checkOverlapping(newLesson, allLesson) {
                    isNewLessonOverlapping(newLesson, it)
                }

            else -> true
        }
    }

    private fun checkOverlapping(
        newLesson: CalendarEvent,
        allLesson: List<CalendarEvent>,
        filter: (CalendarEvent) -> Boolean,
    ) =
        allLesson.filter(filter).any {
            isOverlappingTime(it, newLesson)
        }

    /**
     * Checks if the event in the interval is already cancelled or rescheduled.
     *
     * @param cancelEvent the alteration to be cancel the event.
     * @param cancelEvents the set of cancelled events.
     * @param rescheduleEvents the set of rescheduled events.
     * @param newEvents the set of new events.
     * @return true if the event can be cancelled, false otherwise.
     */
    fun checkEventCanBeCancelled(
        cancelEvent: CancelEvent,
        cancelEvents: Set<CancelEvent>,
        rescheduleEvents: Set<RescheduleEvent>,
        newEvents: Set<NewEvent>,
    ) =
        (
            cancelEvents.filter { it.event == cancelEvent.event } +
                rescheduleEvents.filter { it.originalEvent == cancelEvent.event } +
                newEvents.filter { it.event == cancelEvent.event }
            )
            .all {
                when (it) {
                    is CancelEvent -> !checkIntersectionOfInterval(cancelEvent, it)
                    is RescheduleEvent -> !checkIntersectionOfInterval(cancelEvent, it)
                    is NewEvent -> true
                    else -> true
                }
            }

    /**
     * Checks if the event in the interval is overlapping with the event of the professor or class.
     *
     * @param newEventAlteration the alteration that add a new event.
     * @param newEvents the set of new events.
     * @param rescheduleEvents the set of rescheduled events.
     * @param cancelEvents the set of cancelled events.
     * @param events the function that returns the events of the professor or class.
     * @return true if the event can be added, false otherwise.
     */
    fun checkEventCanBeAdded(
        newEventAlteration: NewEvent,
        newEvents: Set<NewEvent>,
        rescheduleEvents: Set<RescheduleEvent>,
        cancelEvents: Set<CancelEvent>,
        events: (Professor, Class) -> Set<CalendarEvent>,
    ): Boolean {
        val newEvent = newEventAlteration.event
        return if (newEvent is Lesson) {
            !checkLessonIntersection(
                newEventAlteration,
                newEvent,
                newEvents,
                rescheduleEvents,
                cancelEvents,
                events(newEvent.professor, newEvent.studentsClass),
            )
        } else {
            true
        }
    }

    fun checkEventCanBeRescheduled(
        rescheduledEvent: RescheduleEvent,
        alterationEvents: Set<AlterationEvent>,
        newEvents: Set<NewEvent>,
        rescheduleEvents: Set<RescheduleEvent>,
        cancelEvents: Set<CancelEvent>,
        events: (Professor, Class) -> Set<CalendarEvent>,
    ): Boolean {
        val newEvent = rescheduledEvent.event
        val originalEvent = rescheduledEvent.originalEvent
        return originalEvent != newEvent &&
            alterationEvents.filter { it.event == newEvent || it.event == originalEvent }
                .all {
                    !checkIntersectionOfInterval(rescheduledEvent, it)
                } && (newEvent as Lesson?)?.let {
                !checkLessonIntersection(
                    rescheduledEvent,
                    newEvent,
                    newEvents,
                    rescheduleEvents,
                    cancelEvents,
                    events(newEvent.professor, newEvent.studentsClass),
                )
            } ?: true
    }

    private fun checkLessonIntersection(
        newEventAlteration: AlterationEvent,
        newEvent: Lesson,
        newEvents: Set<NewEvent>,
        rescheduleEvents: Set<RescheduleEvent>,
        cancelEvents: Set<CancelEvent>,
        events: Set<CalendarEvent>,
    ): Boolean {
        return when (newEventAlteration) {
            is RescheduleEvent -> {
                areEventsInTimeTableOverlapping(newEvent, events, newEvents, rescheduleEvents, cancelEvents)
            }

            is NewEvent -> {
                areEventsInTimeTableOverlapping(newEvent, events, newEvents, rescheduleEvents, cancelEvents)
            }

            else -> {
                false
            }
        }
    }

    private fun checkIntersectionOfInterval(
        oldAlteration: AlterationEvent,
        newAlteration: AlterationEvent,
    ): Boolean =
        when (oldAlteration) {
            is IntervalOfDaysAlteration -> {
                when (newAlteration) {
                    is IntervalOfDaysAlteration -> {
                        isDateInInterval(
                            newAlteration.initialDate,
                            oldAlteration.initialDate,
                            oldAlteration.finalDate,
                        ) ||
                            isDateInInterval(
                                newAlteration.finalDate,
                                oldAlteration.initialDate,
                                oldAlteration.finalDate,
                            )
                    }

                    is SingleDayAlteration -> {
                        isDateInInterval(
                            newAlteration.date,
                            oldAlteration.initialDate,
                            oldAlteration.finalDate,
                        )
                    }

                    else -> false
                }
            }

            is SingleDayAlteration -> {
                when (newAlteration) {
                    is IntervalOfDaysAlteration -> {
                        isDateInInterval(
                            oldAlteration.date,
                            newAlteration.initialDate,
                            newAlteration.finalDate,
                        )
                    }

                    is SingleDayAlteration -> {
                        oldAlteration.date == newAlteration.date
                    }

                    else -> false
                }
            }

            else -> false
        }
}
