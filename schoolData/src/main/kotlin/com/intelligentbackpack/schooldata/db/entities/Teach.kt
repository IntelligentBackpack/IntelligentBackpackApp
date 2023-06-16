package com.intelligentbackpack.schooldata.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Teaches",
    foreignKeys = [
        ForeignKey(
            entity = Professor::class,
            parentColumns = ["email"],
            childColumns = ["professor_email"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = SchoolClass::class,
            parentColumns = ["name"],
            childColumns = ["class"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = Subject::class,
            parentColumns = ["subject_id"],
            childColumns = ["subject_id"],
            onDelete = ForeignKey.CASCADE,
        ),

    ],
    indices = [
        Index(value = ["professor_email"]),
        Index(value = ["class"]),
        Index(value = ["subject_id"]),
    ],
)
data class Teach(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo("professor_email")
    val professorEmail: String,
    @ColumnInfo("class")
    val schoolClass: String,
    @ColumnInfo("subject_id")
    val subjectId: Int,
)
