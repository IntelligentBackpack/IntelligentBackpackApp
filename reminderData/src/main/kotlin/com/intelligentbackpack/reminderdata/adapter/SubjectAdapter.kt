package com.intelligentbackpack.reminderdata.adapter

import calendar.communication.Subject as RemoteSubject
import com.intelligentbackpack.reminderdata.db.entities.Subject as DBSubject

object SubjectAdapter {

    fun RemoteSubject.fromRemoteToDB(): DBSubject {
        return DBSubject(
            subjectId = id.toInt(),
            name = name,
        )
    }
}
