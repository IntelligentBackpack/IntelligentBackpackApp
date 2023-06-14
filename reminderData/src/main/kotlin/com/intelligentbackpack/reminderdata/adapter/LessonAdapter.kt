package com.intelligentbackpack.reminderdata.adapter

import com.intelligentbackpack.reminderdata.db.entities.Subject
import com.intelligentbackpack.reminderdomain.adapter.EventAdapter
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale
import calendar.communication.Lesson as RemoteLesson
import com.intelligentbackpack.reminderdata.db.entities.Lesson as DBLesson

object LessonAdapter {

    fun RemoteLesson.fromRemoteToDB(): DBLesson {
        return DBLesson(
            id = 0,
            subjectId = materia,
            day = DayOfWeek.valueOf(giorno.uppercase(Locale.ROOT)).value,
            startTime = LocalTime.parse(oraInizio),
            endTime = LocalTime.parse(oraFine),
            fromDate = LocalDate.parse(dataInizio),
            toDate = LocalDate.parse(dataFine),
            professor = professore,
            module = nomeLezione,
        )
    }

    fun DBLesson.fromDBToDomain(subjects: List<Subject>): EventAdapter.Lesson {
        return if (this.fromDate == this.toDate) {
            EventAdapter.DateLessonImpl(
                subject = subjects.first { it.subjectId == subjectId }.name,
                startTime = startTime,
                endTime = endTime,
                date = fromDate,
            )
        } else {
            EventAdapter.WeekLessonImpl(
                subject = subjects.first { it.subjectId == subjectId }.name,
                startTime = startTime,
                endTime = endTime,
                fromDate = fromDate,
                toDate = toDate,
                dayOfWeek = DayOfWeek.of(day),
            )
        }
    }
}
