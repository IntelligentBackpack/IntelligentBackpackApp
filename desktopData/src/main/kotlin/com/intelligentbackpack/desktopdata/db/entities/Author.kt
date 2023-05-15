package com.intelligentbackpack.desktopdata.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Author DB entity
 */
@Entity(tableName = "Authors")
internal class Author(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "author_id")
    val authorId: Int,
    val name: String,
)