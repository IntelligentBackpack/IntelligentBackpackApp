package com.intelligentbackpack.schooldata.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.intelligentbackpack.schooldata.db.entities.Lesson
import com.intelligentbackpack.schooldata.db.entities.Professor
import com.intelligentbackpack.schooldata.db.entities.SchoolClass
import com.intelligentbackpack.schooldata.db.entities.Subject
import com.intelligentbackpack.schooldata.db.entities.Teach
import java.time.LocalDate
import java.time.LocalTime

/**
 * Desktop DAO to access database
 */
@Dao
internal interface SchoolDAO {

    /**
     * Insert a subject
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubject(subject: Subject)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLesson(lesson: Lesson)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProfessor(professor: Professor)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertClass(schoolClass: SchoolClass)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTeach(teach: Teach): Long

    @Query("DELETE FROM Lessons")
    fun deleteLessons()

    /**
     * Get all subjects
     */
    @Query("SELECT * FROM Subjects")
    fun getSubjects(): List<Subject>

    @Query(
        "SELECT * " +
            "FROM Lessons " +
            "WHERE day = :day " +
            "AND start_time = :startTime " +
            "AND end_time = :endTime " +
            "AND from_date = :fromDate " +
            "AND to_date = :toDate",
    )
    fun getLesson(day: Int, startTime: LocalTime, endTime: LocalTime, fromDate: LocalDate, toDate: LocalDate): Lesson?

    @Query("SELECT * FROM Lessons")
    fun getLessons(): List<Lesson>

    @Query("SELECT * FROM Classes")
    fun getClasses(): List<SchoolClass>

    @Query("SELECT * FROM Professors")
    fun getProfessors(): List<Professor>

    @Query("SELECT * FROM Teaches")
    fun getTeaches(): List<Teach>
}
