package com.intelligentbackpack.reminderdata.db

import android.content.Context
import androidx.room.Room
import com.intelligentbackpack.reminderdata.db.converter.LocalDateConverter
import com.intelligentbackpack.reminderdata.db.converter.LocalTimeConverter

/**
 * Helper class for creating reminder database
 */
object ReminderDatabaseHelper {
    /**
     * Create reminder database
     *
     * @param appContext application context
     */
    fun getDatabase(appContext: Context): ReminderDatabase {
        return Room.databaseBuilder(
            appContext,
            ReminderDatabase::class.java,
            "reminder-database",
        )
            .addTypeConverter(LocalTimeConverter())
            .addTypeConverter(LocalDateConverter())
            .fallbackToDestructiveMigration()
            .build()
    }
}
