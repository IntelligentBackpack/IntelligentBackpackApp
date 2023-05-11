package com.intelligentbackpack.desktopdomain.entities.implementations

import com.intelligentbackpack.desktopdomain.entities.Book
import com.intelligentbackpack.desktopdomain.entities.BookCopy
import com.intelligentbackpack.desktopdomain.entities.SchoolSupplyType
import com.intelligentbackpack.desktopdomain.entities.SchoolSupplyTypes

/**
 * Implementation of a book copy.
 *
 * @property rfidCode The RFID code of the book copy.
 * @property book The book of the copy.
 * @property type is automatically set to [SchoolSupplyTypes.BOOK].
 */
internal data class BookCopyImpl(
    override val rfidCode: String,
) : BookCopy {

    override lateinit var book: Book
        private set

    constructor(
        rfidCode: String,
        book: Book
    ) : this(rfidCode) {
        this.book = book
    }

    override val type: SchoolSupplyType = SchoolSupplyTypes.BOOK
}
