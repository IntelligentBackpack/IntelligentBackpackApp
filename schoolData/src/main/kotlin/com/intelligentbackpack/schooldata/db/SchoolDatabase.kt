package com.intelligentbackpack.schooldata.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.intelligentbackpack.schooldata.db.converter.LocalDateConverter
import com.intelligentbackpack.schooldata.db.converter.LocalTimeConverter
import com.intelligentbackpack.schooldata.db.entities.Lesson
import com.intelligentbackpack.schooldata.db.entities.Professor
import com.intelligentbackpack.schooldata.db.entities.SchoolClass
import com.intelligentbackpack.schooldata.db.entities.Subject
import com.intelligentbackpack.schooldata.db.entities.Teach

/**
 * School database
 */
@Database(
    entities = [Subject::class, Lesson::class, Professor::class, SchoolClass::class, Teach::class],
    views = [],
    version = 1,
)
@TypeConverters(LocalDateConverter::class, LocalTimeConverter::class)
abstract class SchoolDatabase : RoomDatabase() {

    /**
     * Get school DAO to access database
     */
    internal abstract fun schoolDao(): SchoolDAO
}
