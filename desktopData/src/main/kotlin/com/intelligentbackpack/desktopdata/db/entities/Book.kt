package com.intelligentbackpack.desktopdata.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Book DB entity
 */
@Entity(tableName = "Books")
internal data class Book(
    @PrimaryKey val isbn: String,
    val title: String
)
