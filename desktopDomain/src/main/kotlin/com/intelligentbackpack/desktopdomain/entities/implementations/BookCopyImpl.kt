package com.intelligentbackpack.desktopdomain.entities.implementations

import com.intelligentbackpack.desktopdomain.entities.BookCopy
import com.intelligentbackpack.desktopdomain.entities.SchoolSupplyType
import com.intelligentbackpack.desktopdomain.entities.SchoolSupplyTypes

/**
 * Implementation of a book copy.
 *
 * @property rfidCode The RFID code of the book copy.
 * @property isbn The ISBN of the book copy.
 * @property title The title of the book copy.
 * @property authors The authors of the book copy.
 * @property type is automatically set to [SchoolSupplyTypes.BOOK].
 */
internal data class BookCopyImpl(
    override val rfidCode: String,
) : BookCopy {

    override lateinit var isbn: String
        private set
    override lateinit var title: String
        private set
    override lateinit var authors: List<String>
        private set

    constructor(
        rfidCode: String,
        isbn: String,
        title: String,
        authors: List<String>,
    ) : this(rfidCode) {
        this.isbn = isbn
        this.title = title
        this.authors = authors
    }

    override val type: SchoolSupplyType = SchoolSupplyTypes.BOOK
}
