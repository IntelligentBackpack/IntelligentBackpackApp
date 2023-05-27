package com.intelligentbackpack.schooldomain.entities.calendar

import com.intelligentbackpack.schooldomain.entities.Class
import com.intelligentbackpack.schooldomain.entities.Subject
import com.intelligentbackpack.schooldomain.entities.person.Professor

/**
 * A lesson.
 *
 * @property subject the subject of the lesson
 * @property module the module of the lesson
 * @property professor the professor of the lesson
 * @property studentsClass the class of the lesson
 */
interface Lesson : CalendarEvent {
    val subject: Subject
    val module: String?
    val professor: Professor
    val studentsClass: Class
}
