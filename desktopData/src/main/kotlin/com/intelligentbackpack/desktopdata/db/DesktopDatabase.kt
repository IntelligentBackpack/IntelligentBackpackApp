package com.intelligentbackpack.desktopdata.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.intelligentbackpack.desktopdata.db.entities.Author
import com.intelligentbackpack.desktopdata.db.entities.Book
import com.intelligentbackpack.desktopdata.db.entities.SchoolSupply
import com.intelligentbackpack.desktopdata.db.entities.SchoolSupplyType
import com.intelligentbackpack.desktopdata.db.entities.Wrote
import com.intelligentbackpack.desktopdata.db.view.BookCopy

/**
 * Desktop database
 */
@Database(
    entities = [Author::class, Book::class, Wrote::class, SchoolSupplyType::class, SchoolSupply::class],
    views = [BookCopy::class],
    version = 1
)
abstract class DesktopDatabase : RoomDatabase() {

    /**
     * Get desktop DAO to access database
     */
    internal abstract fun desktopDao(): DesktopDAO

}
