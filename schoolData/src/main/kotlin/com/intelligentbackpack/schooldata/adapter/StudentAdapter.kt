package com.intelligentbackpack.schooldata.adapter

import com.intelligentbackpack.accessdomain.entities.Role
import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.schooldomain.entities.person.Student

/**
 * Adapter for the student entity.
 */
object StudentAdapter {

    /**
     * Converts a user from the access to student of the school domain.
     *
     * @return the student for the domain.
     */
    fun User.fromAccessToSchool(): Student {
        return if (this.role == Role.STUDENT) {
            Student.create(
                email = email,
                name = name,
                surname = surname,
            )
        } else {
            throw IllegalArgumentException("The user role is not valid.")
        }
    }
}
