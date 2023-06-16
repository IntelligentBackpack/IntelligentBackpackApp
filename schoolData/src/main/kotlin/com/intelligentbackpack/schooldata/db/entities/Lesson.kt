package com.intelligentbackpack.schooldata.db.entities

import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@androidx.room.Entity(
    tableName = "Lessons",
    foreignKeys = [
        ForeignKey(
            entity = Teach::class,
            parentColumns = ["id"],
            childColumns = ["teach_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["teach_id"]),
        Index(value = ["day", "start_time", "end_time", "from_date", "to_date"]),
    ],
)
data class Lesson(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,
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
    @ColumnInfo("teach_id")
    val teachId: Int,
)
