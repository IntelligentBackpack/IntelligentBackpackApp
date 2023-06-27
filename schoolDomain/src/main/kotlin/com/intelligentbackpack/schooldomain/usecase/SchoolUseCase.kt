package com.intelligentbackpack.schooldomain.usecase

import com.intelligentbackpack.accessdomain.entities.Role
import com.intelligentbackpack.accessdomain.exceptions.ActionNotAllowedForUserException
import com.intelligentbackpack.accessdomain.usecase.AccessUseCase
import com.intelligentbackpack.schooldomain.entities.School
import com.intelligentbackpack.schooldomain.entities.calendar.CalendarEvent
import com.intelligentbackpack.schooldomain.entities.calendar.alteration.AlterationEvent
import com.intelligentbackpack.schooldomain.repository.SchoolDomainRepository
import java.time.LocalDate

/**
 * School use case.
 *
 * @param accessUseCase the access use case
 * @param repository the school repository
 */
class SchoolUseCase(private val accessUseCase: AccessUseCase, private val repository: SchoolDomainRepository) {

    /**
     * Downloads the school of the user and its data.
     */
    suspend fun downloadSchool() =
        accessUseCase.getLoggedUser().mapCatching {
            if (it.role == Role.STUDENT || it.role == Role.PROFESSOR) {
                repository.downloadSchool(it)
            } else {
                throw ActionNotAllowedForUserException()
            }
        }

    /**
     * Gets the school view of the user.
     *
     * @return the result of the operation with the school.
     */
    suspend fun getSchoolViewOfUser(): Result<School> {
        return accessUseCase.getLoggedUser().mapCatching {
            if (it.role == Role.STUDENT || it.role == Role.PROFESSOR) {
                repository.getSchool(it)
            } else {
                throw ActionNotAllowedForUserException()
            }
        }
    }

    /**
     * Gets the calendar event of the user for the given date.
     *
     * @param date the date
     * @return the result of the operation with the calendar events.
     */
    suspend fun getUserCalendarEventsForDate(date: LocalDate): Result<List<CalendarEvent>> =
        accessUseCase.getLoggedUser().mapCatching { user ->
            if (user.role == Role.STUDENT || user.role == Role.PROFESSOR) {
                repository.getSchool(user).let { school ->
                    if (user.role == Role.STUDENT) {
                        val student = school.students.find { it.email == user.email }
                        school.classes.find { it.students.contains(student) }?.let {
                            school.calendar?.getStudentsEvents(
                                it,
                                date,
                            )
                        } ?: throw ActionNotAllowedForUserException()
                    } else {
                        school.professors.find { it.email == user.email }?.let {
                            school.calendar?.getProfessorEvents(
                                it,
                                date,
                            )
                        } ?: throw ActionNotAllowedForUserException()
                    }
                }
            } else {
                throw ActionNotAllowedForUserException()
            }
        }

    /**
     * Adds an alteration event to the school calendar.
     *
     * @param alterationEvent the alteration event to add to the calendar.
     * @return the result of the operation.
     */
    suspend fun addAlterationEvent(alterationEvent: AlterationEvent) =
        accessUseCase.getLoggedUser().mapCatching { user ->
            if (user.role == Role.PROFESSOR) {
                repository.getSchool(user).let { school ->
                    school.professors.find { it.email == user.email }?.let {
                        school.calendar?.addAlteration(
                            alterationEvent,
                        )
                        repository.addAlterationEvent(alterationEvent)
                    } ?: throw ActionNotAllowedForUserException()
                }
            } else {
                throw ActionNotAllowedForUserException()
            }
        }

    /**
     * Gets all the events of the user.
     *
     * @return the result of the operation with the events.
     */
    suspend fun userAllSchoolEvents() =
        accessUseCase.getLoggedUser().mapCatching { user ->
            if (user.role == Role.STUDENT || user.role == Role.PROFESSOR) {
                repository.getSchool(user).let { school ->
                    if (user.role == Role.STUDENT) {
                        val student = school.students.find { it.email == user.email }
                        school.classes.find { it.students.contains(student) }?.let { studentClass ->
                            school.calendar?.getAllStudentEvents(studentClass) ?: emptySet()
                        } ?: emptySet()
                    } else {
                        school.professors.find { it.email == user.email }?.let { professor ->
                            school.calendar?.getAllProfessorEvents(professor) ?: emptySet()
                        }
                    }
                } ?: emptySet()
            } else {
                throw ActionNotAllowedForUserException()
            }
        }
}
