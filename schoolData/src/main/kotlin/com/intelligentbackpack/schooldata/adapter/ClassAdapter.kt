package com.intelligentbackpack.schooldata.adapter

import com.intelligentbackpack.schooldomain.entities.School
import com.intelligentbackpack.schooldata.db.entities.SchoolClass as DBClass
import com.intelligentbackpack.schooldomain.entities.Class as DomainClass

object ClassAdapter {

    fun DBClass.fromDBToDomain(school: School): DomainClass {
        return DomainClass.create(
            name = name,
            school = school,
        )
    }
}
