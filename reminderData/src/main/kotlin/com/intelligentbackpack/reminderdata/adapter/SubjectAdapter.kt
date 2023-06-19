package com.intelligentbackpack.reminderdata.adapter

import calendar.communication.Subject as RemoteSubject
import com.intelligentbackpack.reminderdata.db.entities.Subject as DBSubject

/**
 * Adapter for converting [RemoteSubject] to [DBSubject].
 */
object SubjectAdapter {

    /**
     * Converts a [RemoteSubject] to a [DBSubject].
     */
    fun RemoteSubject.fromRemoteToDB(): DBSubject {
        return DBSubject(
            subjectId = id.toInt(),
            name = name,
        )
    }
}
