package com.intelligentbackpack.desktopdata.db.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.intelligentbackpack.desktopdata.db.entities.Author
import com.intelligentbackpack.desktopdata.db.entities.Book
import com.intelligentbackpack.desktopdata.db.entities.Wrote

/**
 * Relation between books and authors DB object
 */
internal data class BookWithAuthors(
    @Embedded val book: Book,
    @Relation(
        parentColumn = "isbn",
        entityColumn = "author_id",
        associateBy = Junction(Wrote::class)
    )
    val authors: List<Author>
)
