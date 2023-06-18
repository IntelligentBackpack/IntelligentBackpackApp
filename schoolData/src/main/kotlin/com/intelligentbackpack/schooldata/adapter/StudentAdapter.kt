package com.intelligentbackpack.schooldata.adapter

import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.schooldomain.entities.person.Student

object StudentAdapter {

    fun User.fromAccessToSchool(): Student {
        return Student.create(
            email = email,
            name = name,
            surname = surname,
        )
    }
}
