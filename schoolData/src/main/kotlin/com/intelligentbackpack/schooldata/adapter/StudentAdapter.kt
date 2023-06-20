package com.intelligentbackpack.schooldata.adapter

import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.schooldomain.entities.person.Student

/**
 * Adapter for the student entity.
 */
object StudentAdapter {

    /**
     * Converts a student from the access to the domain.
     *
     * @return the student for the domain.
     */
    fun User.fromAccessToSchool(): Student {
        return Student.create(
            email = email,
            name = name,
            surname = surname,
        )
    }
}
