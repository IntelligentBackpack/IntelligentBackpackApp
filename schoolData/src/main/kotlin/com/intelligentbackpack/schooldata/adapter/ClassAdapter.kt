package com.intelligentbackpack.schooldata.adapter

import com.intelligentbackpack.schooldata.db.entities.SchoolClass as DBClass
import com.intelligentbackpack.schooldomain.entities.Class as DomainClass

/**
 * Adapter for the class entity.
 */
object ClassAdapter {

    /**
     * Converts a class from the domain to the database.
     *
     * @return the class for the database.
     */
    fun DBClass.fromDBToDomain(): DomainClass {
        return DomainClass.create(
            name = name,
        )
    }
}
