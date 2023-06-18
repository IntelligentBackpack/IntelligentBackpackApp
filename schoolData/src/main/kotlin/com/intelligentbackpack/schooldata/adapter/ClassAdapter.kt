package com.intelligentbackpack.schooldata.adapter

import com.intelligentbackpack.schooldata.db.entities.SchoolClass as DBClass
import com.intelligentbackpack.schooldomain.entities.Class as DomainClass

object ClassAdapter {

    fun DBClass.fromDBToDomain(): DomainClass {
        return DomainClass.create(
            name = name,
        )
    }
}
