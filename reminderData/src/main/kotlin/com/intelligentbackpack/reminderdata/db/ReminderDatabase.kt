package com.intelligentbackpack.reminderdata.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.intelligentbackpack.reminderdata.db.converter.LocalDateConverter
import com.intelligentbackpack.reminderdata.db.converter.LocalTimeConverter
import com.intelligentbackpack.reminderdata.db.entities.Lesson
import com.intelligentbackpack.reminderdata.db.entities.Reminder
import com.intelligentbackpack.reminderdata.db.entities.Subject

/**
 * Desktop database
 */
@Database(
    entities = [Subject::class, Lesson::class, Reminder::class],
    views = [],
    version = 1,
)
@TypeConverters(LocalDateConverter::class, LocalTimeConverter::class)
abstract class ReminderDatabase : RoomDatabase() {

    /**
     * Get reminder DAO to access database
     */
    internal abstract fun reminderDao(): ReminderDAO
}
