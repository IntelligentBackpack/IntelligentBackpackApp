package com.intelligentbackpack.reminderdata.db.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.time.LocalDate

/**
 * Converts a [LocalDate] to a [String] and vice versa for Room.
 */
@ProvidedTypeConverter
class LocalDateConverter {

    /**
     * Converts a [String] to a [LocalDate].
     */
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    /**
     * Converts a [LocalDate] to a [String].
     */
    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): String? {
        return date?.toString()
    }
}
