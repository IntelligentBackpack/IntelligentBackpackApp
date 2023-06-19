package com.intelligentbackpack.reminderdata.db.entities

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

/**
 * Represents a lesson in the database.
 * Foreign key: [subjectId] -> [Subject.subjectId]
 * Indices: ([day], [startTime], [endTime], [fromDate], [toDate]), [subjectId]
 *
 * @property id The ID of the lesson.
 * @property subjectId The ID of the subject.
 * @property module The module of the lesson.
 * @property day The day of the lesson.
 * @property startTime The start time of the lesson.
 * @property endTime The end time of the lesson.
 * @property fromDate The start date of the lesson.
 * @property toDate The end date of the lesson.
 * @property professor The professor of the lesson.
 * @property calendarId The ID of the calendar of the lesson in the remote calendar service.
 *
 */
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
