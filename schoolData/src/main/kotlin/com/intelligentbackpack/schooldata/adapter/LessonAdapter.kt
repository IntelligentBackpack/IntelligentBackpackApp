package com.intelligentbackpack.schooldata.adapter

import com.intelligentbackpack.schooldomain.entities.Class
import com.intelligentbackpack.schooldomain.entities.calendar.CalendarEventFactory.createWeekLesson
import com.intelligentbackpack.schooldomain.entities.calendar.WeekLesson
import com.intelligentbackpack.schooldomain.entities.person.Professor
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale
import calendar.communication.Lesson as RemoteLesson
import com.intelligentbackpack.schooldata.db.entities.Lesson as DBLesson

/**
 * Adapter for the lesson entity.
 */
object LessonAdapter {

    /**
     * Converts a lesson from the remote to the database.
     *
     * @return the lesson for the database.
     */
    fun RemoteLesson.fromRemoteToDB(teachId: Int): DBLesson {
        return DBLesson(
            id = 0,
            day = DayOfWeek.valueOf(giorno.uppercase(Locale.ROOT)).value,
            startTime = LocalTime.parse(oraInizio),
            endTime = LocalTime.parse(oraFine),
            fromDate = LocalDate.parse(dataInizio),
            toDate = LocalDate.parse(dataFine),
            module = nomeLezione,
            teachId = teachId,
        )
    }

    /**
     * Converts a lesson from the database to the domain.
     *
     * @return the lesson for the domain.
     */
    fun DBLesson.fromDBToDomain(professor: Professor, schoolClass: Class, subject: String): WeekLesson {
        return createWeekLesson(
            subject = subject,
            module = module,
            professor = professor,
            studentsClass = schoolClass,
            startTime = startTime,
            endTime = endTime,
            day = DayOfWeek.of(day),
            fromDate = fromDate,
            toDate = toDate,
        )
    }
}
