package com.intelligentbackpack.schooldomain.entities.calendar

import com.intelligentbackpack.schooldomain.entities.Class
import com.intelligentbackpack.schooldomain.entities.calendar.alteration.AlterationEvent
import com.intelligentbackpack.schooldomain.entities.person.Professor
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * A map of [DayOfWeek] to a list of [WeekLesson]s.
 */
typealias TimeTable = Map<DayOfWeek, List<WeekLesson>>

/**
 * A school calendar is a calendar that contains the schedule of a school time table and the alterations of the schedule.
 *
 * @property schoolYear the school year of the calendar
 * @property studentsTimeTableLesson the time table of the students
 * @property professorsTimeTableLesson the time table of the professors
 * @property alterations the alterations of the calendar
 */
interface SchoolCalendar {
    val schoolYear: String
    val studentsTimeTableLesson: Map<Class, TimeTable>
    val professorsTimeTableLesson: Map<Professor, TimeTable>
    val alterations: Set<AlterationEvent>

    /**
     * Adds lessons to the time table of a class.
     *
     * @param lessons the lessons to add
     */
    fun addLessons(lessons: Set<WeekLesson>): SchoolCalendar

    /**
     * Adds an alteration to the calendar.
     *
     * @param alteration the alteration to add
     */
    fun addAlteration(alteration: AlterationEvent): SchoolCalendar

    /**
     * Gets the events of a class on a certain date.
     *
     * @param studentClass the class to get the events of
     * @param date the date to get the events of
     * @return the events of the class on the date in chronological order
     */
    fun getStudentsEvents(studentClass: Class, date: LocalDate): List<CalendarEvent>

    /**
     * Gets the events of a professor on a certain date.
     *
     * @param professor the professor to get the events of
     * @param date the date to get the events of
     * @return the events of the professor on the date in chronological order
     */
    fun getProfessorEvents(professor: Professor, date: LocalDate): List<CalendarEvent>

    /**
     * Gets all the events of a class.
     *
     * @param studentClass the class to get the events of
     * @return the events of the class
     */
    fun getAllStudentEvents(studentClass: Class): Set<CalendarEvent>

    /**
     * Gets all the events of a professor.
     *
     * @param professor the professor to get the events of
     * @return the events of the professor
     */
    fun getAllProfessorEvents(professor: Professor): Set<CalendarEvent>

    companion object {

        /**
         * Creates a school calendar.
         *
         * @param schoolYear the school year of the calendar
         * @return the created school calendar
         * @throws IllegalArgumentException if the school year is empty
         */
        fun create(
            schoolYear: String,
        ): SchoolCalendar {
            if (schoolYear.isEmpty()) {
                throw IllegalArgumentException("schoolYear must not be empty")
            } else {
                return SchoolCalendarImpl(schoolYear)
            }
        }
    }
}
