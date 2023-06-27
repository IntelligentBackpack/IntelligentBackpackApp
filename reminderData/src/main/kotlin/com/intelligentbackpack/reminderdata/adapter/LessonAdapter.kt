package com.intelligentbackpack.reminderdata.adapter

import com.intelligentbackpack.reminderdata.db.entities.Subject
import com.intelligentbackpack.reminderdomain.adapter.EventAdapter
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale
import calendar.communication.Lesson as RemoteLesson
import com.intelligentbackpack.reminderdata.db.entities.Lesson as DBLesson

/**
 * Adapter for converting [RemoteLesson] to [DBLesson] and vice versa or [DBLesson] to [EventAdapter.Lesson] and vice versa.
 */
object LessonAdapter {

    /**
     * Converts a [RemoteLesson] to a [DBLesson].
     */
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
            calendarId = idCalendario,
        )
    }

    /**
     * Converts a [DBLesson] to a [RemoteLesson].
     */
    fun DBLesson.fromDBToRemote(): RemoteLesson {
        return RemoteLesson.newBuilder()
            .setMateria(this.subjectId)
            .setGiorno(DayOfWeek.of(this.day).name)
            .setOraInizio(this.startTime.toString())
            .setOraFine(this.endTime.toString())
            .setDataInizio(this.fromDate.toString())
            .setDataFine(this.toDate.toString())
            .setProfessore(this.professor)
            .setNomeLezione(this.module)
            .setIDCalendario(this.calendarId)
            .build()
    }

    /**
     * Converts a [DBLesson] to a [EventAdapter.Lesson].
     * if the [DBLesson] is a [DBLesson] with the same [DBLesson.fromDate] and [DBLesson.toDate] then it will be converted to a [EventAdapter.DateLessonImpl].
     * if the [DBLesson] is a [DBLesson] with different [DBLesson.fromDate] and [DBLesson.toDate] then it will be converted to a [EventAdapter.WeekLessonImpl].
     *
     * @param subjects The list of subjects to get the name of the subject from the subject id.
     * @return The converted [DBLesson] to a [EventAdapter.Lesson].
     */
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

    /**
     * Converts a [EventAdapter.Lesson] to a [DBLesson].
     *
     * @param subjects The list of subjects to get the subject id from the subject name.
     * @return The converted [EventAdapter.Lesson] to a [DBLesson].
     */
    fun EventAdapter.Lesson.fromDomainToDB(subjects: List<Subject>): DBLesson {
        return when (this) {
            is EventAdapter.DateLessonImpl -> DBLesson(
                id = 0,
                subjectId = subjects.first { it.name == subject }.subjectId,
                day = date.dayOfWeek.value,
                startTime = startTime,
                endTime = endTime,
                fromDate = date,
                toDate = date,
                professor = "",
                module = "",
                calendarId = 0,
            )

            is EventAdapter.WeekLessonImpl -> DBLesson(
                id = 0,
                subjectId = subjects.first { it.name == subject }.subjectId,
                day = dayOfWeek.value,
                startTime = startTime,
                endTime = endTime,
                fromDate = fromDate,
                toDate = toDate,
                professor = "",
                module = "",
                calendarId = 0,
            )

            else -> throw IllegalStateException("Unknown type of lesson")
        }
    }
}
