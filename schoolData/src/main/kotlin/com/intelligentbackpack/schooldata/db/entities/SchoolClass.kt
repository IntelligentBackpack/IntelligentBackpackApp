package com.intelligentbackpack.schooldata.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "Classes",
)
data class SchoolClass(
    @PrimaryKey
    val name: String,
    @ColumnInfo(name = "calendar_id")
    val calendarId: Int,
)
