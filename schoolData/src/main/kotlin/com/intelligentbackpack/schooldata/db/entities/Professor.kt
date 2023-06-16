package com.intelligentbackpack.schooldata.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "Professors",
)
data class Professor(
    @PrimaryKey
    val email: String,
    val name: String,
    val surname: String,
)
