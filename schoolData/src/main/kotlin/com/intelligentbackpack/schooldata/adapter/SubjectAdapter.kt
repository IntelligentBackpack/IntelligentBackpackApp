package com.intelligentbackpack.schooldata.adapter

import calendar.communication.Subject as RemoteSubject
import com.intelligentbackpack.schooldata.db.entities.Subject as DBSubject

/**
 * Adapter for the subject entity.
 */
object SubjectAdapter {

    /**
     * Converts a subject from the remote to the database.
     *
     * @return the subject for the database.
     */
    fun RemoteSubject.fromRemoteToDB(): DBSubject {
        return DBSubject(
            subjectId = id.toInt(),
            name = name,
        )
    }
}
