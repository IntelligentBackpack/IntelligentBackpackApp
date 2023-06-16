package com.intelligentbackpack.schooldata.db

import android.content.Context
import androidx.room.Room
import com.intelligentbackpack.schooldata.db.converter.LocalDateConverter
import com.intelligentbackpack.schooldata.db.converter.LocalTimeConverter

/**
 * Helper class for creating reminder database
 */
object SchoolDatabaseHelper {
    /**
     * Create reminder database
     *
     * @param appContext application context
     */
    fun getDatabase(appContext: Context): SchoolDatabase {
        return Room.databaseBuilder(
            appContext,
            SchoolDatabase::class.java,
            "school-database",
        )
            .addTypeConverter(LocalTimeConverter())
            .addTypeConverter(LocalDateConverter())
            .fallbackToDestructiveMigration()
            .build()
    }
}
