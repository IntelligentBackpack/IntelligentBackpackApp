package com.intelligentbackpack.schooldata.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "Subjects",
)
data class Subject(
    @PrimaryKey
    @ColumnInfo(name = "subject_id")
    val subjectId: Int,
    val name: String,
)
