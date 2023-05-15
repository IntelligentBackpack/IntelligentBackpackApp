package com.intelligentbackpack.desktopdata.db.view

import androidx.room.ColumnInfo
import androidx.room.DatabaseView

/**
 * Book copy DB view
 */
@DatabaseView(
    "SELECT rfid, School_supplies.isbn, title, in_backpack, School_supply_types.type AS type_name " +
            "FROM School_supplies INNER JOIN  School_supply_types ON School_supplies.type = School_supply_types.type_id " +
            "INNER JOIN Books ON School_supplies.isbn = Books.isbn " +
            "WHERE type_id = 0",
    viewName = "Book_copies"
)
data class BookCopy(
    val rfid: String,
    val isbn: String,
    val title: String,
    @ColumnInfo(name = "type_name")
    val typeName: String,
    @ColumnInfo(name = "in_backpack")
    val inBackpack: Boolean
)
