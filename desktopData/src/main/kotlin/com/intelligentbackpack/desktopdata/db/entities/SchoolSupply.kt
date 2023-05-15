package com.intelligentbackpack.desktopdata.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * School supply DB entity
 */
@Entity(
    tableName = "School_supplies",
    foreignKeys = [
        ForeignKey(
            entity = SchoolSupplyType::class,
            parentColumns = arrayOf("type_id"),
            childColumns = arrayOf("type"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Book::class,
            parentColumns = arrayOf("isbn"),
            childColumns = arrayOf("isbn"),
            onDelete = ForeignKey.CASCADE
        ),
    ],
    indices = [
        Index(value = ["isbn"], unique = false),
        Index(value = ["type"], unique = false)
    ]
)
internal data class SchoolSupply(
    @PrimaryKey val rfid: String,
    val type: Int,
    val isbn: String?,
    @ColumnInfo(name = "in_backpack")
    val inBackpack: Boolean
)