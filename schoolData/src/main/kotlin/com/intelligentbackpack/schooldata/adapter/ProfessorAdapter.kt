package com.intelligentbackpack.schooldata.adapter

import calendar.communication.UserInformations
import com.intelligentbackpack.schooldata.db.entities.Professor as DBProfessor
import com.intelligentbackpack.schooldomain.entities.person.Professor as DomainProfessor

object ProfessorAdapter {

    fun UserInformations.professorFromRemoteToDB(): DBProfessor {
        return DBProfessor(
            email = emailUser,
            name = nome,
            surname = cognome,
        )
    }

    fun DBProfessor.fromDBToDomain(): DomainProfessor {
        return DomainProfessor.create(
            email = email,
            name = name,
            surname = surname,
        )
    }
}
