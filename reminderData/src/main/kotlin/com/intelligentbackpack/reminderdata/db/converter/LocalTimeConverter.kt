package com.intelligentbackpack.reminderdata.db.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.time.LocalTime

@ProvidedTypeConverter
class LocalTimeConverter {
    @TypeConverter
    fun fromTimestamp(value: String?): LocalTime? {
        return value?.let { LocalTime.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalTime?): String? {
        return date?.toString()
    }
}
