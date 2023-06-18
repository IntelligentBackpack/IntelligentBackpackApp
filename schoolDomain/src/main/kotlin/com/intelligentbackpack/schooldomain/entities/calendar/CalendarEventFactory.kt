package com.intelligentbackpack.schooldomain.entities.calendar

import com.intelligentbackpack.schooldomain.entities.Class
import com.intelligentbackpack.schooldomain.entities.Subject
import com.intelligentbackpack.schooldomain.entities.person.Professor
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

/**
 * Object that creates calendar events.
 */
object CalendarEventFactory {

    private data class WeekLessonImpl(
        override val professor: Professor,
        override val studentsClass: Class,
        override val startTime: LocalTime,
        override val endTime: LocalTime,
        override val day: DayOfWeek,
        override val fromDate: LocalDate,
        override val toDate: LocalDate,
    ) : WeekLesson {
        override var subject: Subject = ""
            private set
        override var module: String? = null
            private set

        constructor(
            subject: Subject,
            module: String?,
            professor: Professor,
            studentsClass: Class,
            startTime: LocalTime,
            endTime: LocalTime,
            day: DayOfWeek,
            fromDate: LocalDate,
            toDate: LocalDate,
        ) : this(
            professor,
            studentsClass,
            startTime,
            endTime,
            day,
            fromDate,
            toDate,
        ) {
            this.subject = subject
            this.module = module
        }
    }

    private data class DateLessonImpl(
        override val professor: Professor,
        override val studentsClass: Class,
        override val startTime: LocalTime,
        override val endTime: LocalTime,
        override val date: LocalDate,
    ) : DateLesson {
        override var subject: Subject = ""
            private set
        override var module: String? = null
            private set

        constructor(
            subject: Subject,
            module: String?,
            professor: Professor,
            studentsClass: Class,
            startTime: LocalTime,
            endTime: LocalTime,
            date: LocalDate,
        ) : this(
            professor,
            studentsClass,
            startTime,
            endTime,
            date,
        ) {
            this.subject = subject
            this.module = module
        }
    }

    /**
     * Creates a [WeekLesson].
     *
     * @param professor the professor of the lesson
     * @param studentsClass the class of the lesson
     * @param startTime the start time of the lesson
     * @param endTime the end time of the lesson
     * @param day the day of the week the lesson happens
     * @param fromDate the date from which the lesson happens
     * @param toDate the date until which the lesson happens
     * @param subject the subject of the lesson
     * @param module the module of the lesson
     * @return the created [WeekLesson]
     * @throws IllegalArgumentException if the subject is blank, if the start time is after the end time, if the from date is after the to date, or if the professor does not teach the subject
     */
    fun createWeekLesson(
        professor: Professor,
        studentsClass: Class,
        startTime: LocalTime,
        endTime: LocalTime,
        day: DayOfWeek,
        fromDate: LocalDate,
        toDate: LocalDate,
        subject: Subject,
        module: String? = null,
    ): WeekLesson {
        if (subject.isBlank()) {
            throw IllegalArgumentException("subject cannot be blank")
        } else if (startTime.isAfter(endTime)) {
            throw IllegalArgumentException("startTime cannot be after endTime")
        } else if (fromDate.isAfter(toDate)) {
            throw IllegalArgumentException("fromDate cannot be after toDate")
        } else {
            return WeekLessonImpl(
                subject,
                module,
                professor,
                studentsClass,
                startTime,
                endTime,
                day,
                fromDate,
                toDate,
            )
        }
    }

    /**
     * Creates a [DateLesson].
     *
     * @param professor the professor of the lesson
     * @param studentsClass the class of the lesson
     * @param startTime the start time of the lesson
     * @param endTime the end time of the lesson
     * @param date the date of the lesson
     * @param subject the subject of the lesson
     * @param module the module of the lesson
     * @return the created [DateLesson]
     * @throws IllegalArgumentException if the subject is blank or if the start time is after the end time
     */
    fun createDateLesson(
        professor: Professor,
        studentsClass: Class,
        startTime: LocalTime,
        endTime: LocalTime,
        date: LocalDate,
        subject: Subject,
        module: String? = null,
    ): DateLesson {
        if (subject.isBlank()) {
            throw IllegalArgumentException("subject cannot be blank")
        } else if (startTime.isAfter(endTime)) {
            throw IllegalArgumentException("startTime cannot be after endTime")
        } else {
            return DateLessonImpl(
                subject,
                module,
                professor,
                studentsClass,
                startTime,
                endTime,
                date,
            )
        }
    }
}
