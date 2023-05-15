package com.intelligentbackpack.desktopdata.adapter

import com.intelligentbackpack.desktopdata.db.entities.Author as AuthorDB
import com.intelligentbackpack.desktopdomain.entities.Author as AuthorDomain

/**
 * Adapter for author
 */
internal object AuthorAdapter {

    /**
     * Convert from domain to DB
     */
    fun AuthorDB.fromDBToDomain(): AuthorDomain = this.name
}
