package com.intelligentbackpack.reminderdata.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a subject.
 *
 * @property subjectId The ID of the subject.
 * @property name The name of the subject.
 */
@Entity(
    tableName = "Subjects",
)
data class Subject(
    @PrimaryKey
    @ColumnInfo(name = "subject_id")
    val subjectId: Int,
    val name: String,
)
