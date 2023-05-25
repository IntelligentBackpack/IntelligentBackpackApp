package com.intelligentbackpack.desktopdata.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * School supply type DB entity
 */
@Entity(tableName = "School_supply_types")
internal data class SchoolSupplyType(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "type_id")
    val typeId: Int,
    @ColumnInfo(name = "type") val type: String,
)
