package com.intelligentbackpack.reminderdata.db.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.time.LocalTime

/**
 * Converts a [LocalTime] to a [String] and vice versa for Room.
 */
@ProvidedTypeConverter
class LocalTimeConverter {

    /**
     * Converts a [String] to a [LocalTime].
     */
    @TypeConverter
    fun fromTimestamp(value: String?): LocalTime? {
        return value?.let { LocalTime.parse(it) }
    }

    /**
     * Converts a [LocalTime] to a [String].
     */
    @TypeConverter
    fun dateToTimestamp(date: LocalTime?): String? {
        return date?.toString()
    }
}
