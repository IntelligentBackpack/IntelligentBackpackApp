package com.intelligentbackpack.schooldomain.repository

import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.schooldomain.entities.School
import com.intelligentbackpack.schooldomain.entities.calendar.alteration.AlterationEvent

/**
 * Repository for the school module.
 *
 */
interface SchoolRepository {
    /**
     * Gets the school of the user.
     *
     * @param user the user
     * @return the school
     */
    suspend fun getSchool(user: User): School

    /**
     * Downloads the school of the user.
     *
     * @param user the user
     */
    suspend fun downloadSchool(user: User)

    /**
     * Adds an alteration event.
     *
     * @param alterationEvent the alteration event
     */
    suspend fun addAlterationEvent(alterationEvent: AlterationEvent)
}
