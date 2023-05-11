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

    /**
     * The book of the copy.
     */
    override lateinit var book: Book
        private set

    /**
     * The type of the school supply is automatically set to [SchoolSupplyTypes.BOOK].
     */
    override val type: SchoolSupplyType = SchoolSupplyTypes.BOOK

    /**
     * Constructor for a book copy.
     *
     * @param rfidCode The RFID code of the book copy.
     * @param book The book of the copy.
     */
    constructor(
        rfidCode: String,
        book: Book
    ) : this(rfidCode) {
        this.book = book
    }
}
