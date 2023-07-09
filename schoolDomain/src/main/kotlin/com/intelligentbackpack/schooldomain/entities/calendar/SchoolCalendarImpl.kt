package com.intelligentbackpack.schooldomain.entities.calendar

import com.intelligentbackpack.schooldomain.entities.Class
import com.intelligentbackpack.schooldomain.entities.calendar.alteration.AlterationEvent
import com.intelligentbackpack.schooldomain.entities.calendar.alteration.CancelEvent
import com.intelligentbackpack.schooldomain.entities.calendar.alteration.NewEvent
import com.intelligentbackpack.schooldomain.entities.calendar.alteration.RescheduleEvent
import com.intelligentbackpack.schooldomain.entities.person.Professor
import com.intelligentbackpack.schooldomain.exceptions.EventCantBeAddedException
import com.intelligentbackpack.schooldomain.exceptions.EventCantBeCancelledException
import com.intelligentbackpack.schooldomain.exceptions.EventCantBeRescheduleException
import com.intelligentbackpack.schooldomain.exceptions.EventNotFoundException
import com.intelligentbackpack.schooldomain.exceptions.EventOverlappingException
import com.intelligentbackpack.schooldomain.policy.AlterationPolicy.checkEventCanBeAdded
import com.intelligentbackpack.schooldomain.policy.AlterationPolicy.checkEventCanBeCancelled
import com.intelligentbackpack.schooldomain.policy.AlterationPolicy.checkEventCanBeRescheduled
import com.intelligentbackpack.schooldomain.policy.CalendarPolicy.areLessonsInTimeTableOverlapping
import java.time.LocalDate

internal data class SchoolCalendarImpl(
    override val schoolYear: String,
) : SchoolCalendar {

    private var lessons: Set<WeekLesson> = setOf()
    override val studentsTimeTableLesson: Map<Class, TimeTable>
        get() = lessons
            .groupBy { it.studentsClass }
            .mapValues { (_, lessons) ->
                lessons
                    .groupBy { it.day }
                    .mapValues { (_, lessons) -> lessons.sortedBy { it.startTime } }
            }
    override val professorsTimeTableLesson: Map<Professor, TimeTable>
        get() = lessons
            .groupBy { it.professor }
            .mapValues { (_, lessons) ->
                lessons
                    .groupBy { it.day }
                    .mapValues { (_, lessons) -> lessons.sortedBy { it.startTime } }
            }

    private var cancelEvents: Set<CancelEvent> = setOf()

    private var newEvents: Set<NewEvent> = setOf()

    private var rescheduleEvents: Set<RescheduleEvent> = setOf()

    override val alterations: Set<AlterationEvent>
        get() = cancelEvents + newEvents + rescheduleEvents

    private fun copy(
        lessons: Set<WeekLesson> = this.lessons,
        cancelEvents: Set<CancelEvent> = this.cancelEvents,
        newEvents: Set<NewEvent> = this.newEvents,
        rescheduleEvents: Set<RescheduleEvent> = this.rescheduleEvents,
    ) = SchoolCalendarImpl(
        schoolYear = schoolYear,
    ).apply {
        this.lessons = lessons
        this.cancelEvents = cancelEvents
        this.newEvents = newEvents
        this.rescheduleEvents = rescheduleEvents
    }

    override fun addLessons(lessons: Set<WeekLesson>): SchoolCalendar {
        check(lessons.any { lesson -> this.lessons.any { it == lesson } }) {
            "lessons must not be in current lessons"
        }
        val checkOverlappingStudents = lessons.any { lesson ->
            areLessonsInTimeTableOverlapping(
                lessons,
                studentsTimeTableLesson[lesson.studentsClass] ?: emptyMap(),
            ) {
                it.studentsClass == lesson.studentsClass
            }
        }
        val checkOverlappingProfessors = lessons.any { lesson ->
            areLessonsInTimeTableOverlapping(
                lessons,
                professorsTimeTableLesson[lesson.professor] ?: emptyMap(),
            ) {
                it.professor == lesson.professor
            }
        }
        if (checkOverlappingStudents || checkOverlappingProfessors) {
            throw EventOverlappingException()
        } else {
            return copy(lessons = this.lessons + lessons)
        }
    }

    override fun addAlteration(alteration: AlterationEvent): SchoolCalendar {
        when (alteration) {
            is CancelEvent -> {
                val cancelEvent = alteration.event
                if (!lessons.contains(cancelEvent)) {
                    throw EventNotFoundException()
                } else {
                    if (checkEventCanBeCancelled(alteration, cancelEvents, rescheduleEvents, newEvents)) {
                        return copy(cancelEvents = this.cancelEvents + alteration)
                    } else {
                        throw EventCantBeCancelledException()
                    }
                }
            }

            is NewEvent -> {
                val newEvent = alteration.event
                if (lessons.contains(newEvent)) {
                    throw EventNotFoundException()
                } else {
                    val check = checkEventCanBeAdded(
                        alteration,
                        newEvents,
                        rescheduleEvents,
                        cancelEvents,
                    ) { professor, studentClass ->
                        (
                            professorsTimeTableLesson[professor]
                                ?.values
                                ?.flatten()
                                ?.toSet()
                                ?: emptySet()
                            ) +
                            (
                                studentsTimeTableLesson[studentClass]
                                    ?.values
                                    ?.flatten()
                                    ?.toSet()
                                    ?: emptySet()
                                )
                    }
                    if (check) {
                        return copy(newEvents = this.newEvents + alteration)
                    } else {
                        throw EventCantBeAddedException()
                    }
                }
            }

            is RescheduleEvent -> {
                val rescheduleEvent = alteration.originalEvent
                if (!lessons.contains(rescheduleEvent)) {
                    throw EventNotFoundException()
                } else {
                    val check = checkEventCanBeRescheduled(
                        alteration,
                        alterations,
                        newEvents,
                        rescheduleEvents,
                        cancelEvents,
                    ) { professor, studentClass ->
                        (
                            professorsTimeTableLesson[professor]
                                ?.values
                                ?.flatten()
                                ?.toSet()
                                ?: emptySet()
                            ) +
                            (
                                studentsTimeTableLesson[studentClass]
                                    ?.values
                                    ?.flatten()
                                    ?.toSet()
                                    ?: emptySet()
                                )
                    }
                    if (check) {
                        return copy(rescheduleEvents = this.rescheduleEvents + alteration)
                    } else {
                        throw EventCantBeRescheduleException()
                    }
                }
            }

            else -> throw IllegalArgumentException("alteration must be a CancelEvent, NewEvent or RescheduleEvent")
        }
    }

    private fun getEvents(events: TimeTable?, date: LocalDate): List<CalendarEvent> =
        (
            (
                events?.get(date.dayOfWeek)
                    ?.filter { lesson ->
                        date in lesson.fromDate..lesson.toDate
                    }
                    ?.filter { lesson ->
                        lesson !in cancelEvents.filter { it.isEventCancelledOnDate(date) }.map { it.event }
                    }
                    ?.filter { lesson ->
                        lesson !in rescheduleEvents
                            .filter { it.wasEventRescheduledOnDate(date) }
                            .map { it.originalEvent }
                    }
                    ?: emptyList()
                ) +
                newEvents.filter { it.isNewEventOnDate(date) }.map { it.event } +
                rescheduleEvents.filter { it.isNewEventOnDate(date) }.map { it.event }
            ).sortedBy { it.startTime }

    override fun getStudentsEvents(studentClass: Class, date: LocalDate): List<CalendarEvent> =
        getEvents(studentsTimeTableLesson[studentClass], date)

    override fun getProfessorEvents(professor: Professor, date: LocalDate): List<CalendarEvent> =
        getEvents(professorsTimeTableLesson[professor], date)

    override fun getAllStudentEvents(studentClass: Class): Set<CalendarEvent> {
        val events = studentsTimeTableLesson[studentClass]?.values?.flatten()?.toSet() ?: emptySet()
        return events +
            newEvents
                .filter {
                    isLessonWithProperty(it.event) { lesson -> lesson.studentsClass == studentClass }
                }
                .map { it.event } +
            rescheduleEvents
                .filter {
                    isLessonWithProperty(it.event) { lesson -> lesson.studentsClass == studentClass }
                }
                .map { it.event }
    }

    override fun getAllProfessorEvents(professor: Professor): Set<CalendarEvent> {
        val events = professorsTimeTableLesson[professor]?.values?.flatten()?.toSet() ?: emptySet()
        return events +
            newEvents
                .filter {
                    isLessonWithProperty(it.event) { lesson -> lesson.professor == professor }
                }
                .map { it.event } +
            rescheduleEvents
                .filter {
                    isLessonWithProperty(it.event) { lesson -> lesson.professor == professor }
                }
                .map { it.event }
    }

    private fun isLessonWithProperty(
        event: CalendarEvent,
        property: (Lesson) -> Boolean,
    ): Boolean =
        event is Lesson && property(event)
}
