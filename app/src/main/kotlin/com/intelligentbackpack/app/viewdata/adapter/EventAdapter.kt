package com.intelligentbackpack.app.viewdata.adapter

import com.intelligentbackpack.app.viewdata.EventView
import com.intelligentbackpack.schooldomain.entities.Class
import com.intelligentbackpack.schooldomain.entities.calendar.CalendarEvent
import com.intelligentbackpack.schooldomain.entities.calendar.CalendarEventFactory
import com.intelligentbackpack.schooldomain.entities.calendar.DateLesson
import com.intelligentbackpack.schooldomain.entities.calendar.WeekLesson
import com.intelligentbackpack.schooldomain.entities.person.Professor
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

/**
 * Adapter for the calendar event entity.
 */
object EventAdapter {

    /**
     * Converts a calendar event from the domain to the view.
     *
     * @return the calendar event for the view.
     */
    fun CalendarEvent.fromDomainToView(index: Int): EventView? =
        when (this) {
            is WeekLesson -> EventView(
                index = index,
                from = fromDate.toString(),
                to = toDate.toString(),
                date = null,
                subject = subject,
                module = module ?: "",
                day = day.name,
                professor = professor.email,
                professorName = professor.name,
                professorSurname = professor.surname,
                startTime = startTime.toString(),
                endTime = endTime.toString(),
                studentClass = studentsClass.name,
            )

            is DateLesson -> EventView(
                index = index,
                from = null,
                to = null,
                date = date.toString(),
                subject = subject,
                module = module ?: "",
                day = date.dayOfWeek.name,
                professor = professor.email,
                professorName = professor.name,
                professorSurname = professor.surname,
                startTime = startTime.toString(),
                endTime = endTime.toString(),
                studentClass = studentsClass.name,
            )

            else -> null
        }

    /**
     * Converts a calendar event from the view to the domain.
     *
     * @return the calendar event for the domain.
     */
    fun EventView.fromViewToDomain(): CalendarEvent? {
        val professor = Professor.create(
            email = professor,
            name = professorName,
            surname = professorSurname,
        )
        val module = module.ifEmpty { null }
        val studentClass = Class.create(studentClass)
        return when {
            from != null && to != null -> CalendarEventFactory.createWeekLesson(
                fromDate = LocalDate.parse(from),
                toDate = LocalDate.parse(to),
                subject = subject,
                module = module,
                professor = professor,
                day = DayOfWeek.valueOf(day),
                startTime = LocalTime.parse(startTime),
                endTime = LocalTime.parse(endTime),
                studentsClass = studentClass,
            )

            date != null -> CalendarEventFactory.createDateLesson(
                date = LocalDate.parse(date),
                subject = subject,
                startTime = LocalTime.parse(startTime),
                endTime = LocalTime.parse(endTime),
                professor = professor,
                module = module,
                studentsClass = studentClass,
            )

            else -> null
        }
    }
}
