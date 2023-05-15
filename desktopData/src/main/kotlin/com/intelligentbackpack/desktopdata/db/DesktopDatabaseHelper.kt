package com.intelligentbackpack.desktopdata.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.intelligentbackpack.desktopdomain.entities.SchoolSupplyTypes

/**
 * Helper class for creating desktop database
 */
object DesktopDatabaseHelper {
    /**
     * Create desktop database with default type (book)
     *
     * @param appContext application context
     */
    fun getDatabase(appContext: Context): DesktopDatabase {
        return Room.databaseBuilder(
            appContext,
            DesktopDatabase::class.java, "desktop-database"
        ).addCallback(
            object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    db.execSQL("INSERT INTO School_supply_types (type_id, type) VALUES (0, '${SchoolSupplyTypes.BOOK}')")
                }
            })
            .fallbackToDestructiveMigration()
            .build()
    }
}