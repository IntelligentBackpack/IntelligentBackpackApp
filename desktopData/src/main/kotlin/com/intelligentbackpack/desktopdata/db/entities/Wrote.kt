package com.intelligentbackpack.desktopdata.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Relation between books and authors DB entity
 */
@Entity(
    tableName = "Wrote",
    foreignKeys = [ForeignKey(
        entity = Book::class,
        parentColumns = arrayOf("isbn"),
        childColumns = arrayOf("isbn"),
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Author::class,
        parentColumns = arrayOf("author_id"),
        childColumns = arrayOf("author_id"),
        onDelete = ForeignKey.CASCADE
    )],
    primaryKeys = ["isbn", "author_id"],
    indices = [
        Index(value = ["isbn"], unique = false),
        Index(value = ["author_id"], unique = false)
    ]
)
internal data class Wrote(
    val isbn: String,
    @ColumnInfo(name = "author_id")
    val authorId: Int
)
