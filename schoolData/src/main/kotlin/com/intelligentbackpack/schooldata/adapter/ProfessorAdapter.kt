package com.intelligentbackpack.schooldata.adapter

import calendar.communication.UserInformations
import com.intelligentbackpack.schooldata.db.entities.Professor as DBProfessor
import com.intelligentbackpack.schooldomain.entities.person.Professor as DomainProfessor

/**
 * Adapter for the professor entity.
 */
object ProfessorAdapter {

    /**
     * Converts a professor from the remote to the database.
     *
     * @return the professor for the database.
     */
    fun UserInformations.professorFromRemoteToDB(): DBProfessor {
        return DBProfessor(
            email = emailUser,
            name = nome,
            surname = cognome,
        )
    }

    /**
     * Converts a professor from the database to the domain.
     *
     * @return the professor for the domain.
     */
    fun DBProfessor.fromDBToDomain(): DomainProfessor {
        return DomainProfessor.create(
            email = email,
            name = name,
            surname = surname,
        )
    }
}
