package com.intelligentbackpack.schooldata.adapter

import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.schooldomain.entities.Class
import com.intelligentbackpack.schooldomain.entities.person.Student

object StudentAdapter {

    fun User.fromAccessToSchool(schoolClass: Class): Student {
        return Student.create(
            email = email,
            name = name,
            surname = surname,
            studentClass = schoolClass,
        )
    }
}
