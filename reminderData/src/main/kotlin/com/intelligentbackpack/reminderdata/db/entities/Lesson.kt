package com.intelligentbackpack.reminderdata.db.entities

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@androidx.room.Entity(
    tableName = "Lessons",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = Subject::class,
            parentColumns = ["subject_id"],
            childColumns = ["subject_id"],
            onDelete = androidx.room.ForeignKey.CASCADE,
        ),
    ],
    indices = [
        androidx.room.Index(value = ["subject_id"]),
        androidx.room.Index(value = ["day", "start_time", "end_time", "from_date", "to_date"], unique = true),
    ],
)
data class Lesson(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "subject_id")
    val subjectId: Int,
    val module: String,
    val day: Int,
    @ColumnInfo(name = "start_time")
    val startTime: LocalTime,
    @ColumnInfo(name = "end_time")
    val endTime: LocalTime,
    @ColumnInfo(name = "from_date")
    val fromDate: LocalDate,
    @ColumnInfo(name = "to_date")
    val toDate: LocalDate,
    val professor: String,
    @ColumnInfo(name = "calendar_id")
    val calendarId: Int,
)
