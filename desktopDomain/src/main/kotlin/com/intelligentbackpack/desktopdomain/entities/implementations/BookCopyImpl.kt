package com.intelligentbackpack.desktopdomain.entities.implementations

import com.intelligentbackpack.desktopdomain.entities.BookCopy
import com.intelligentbackpack.desktopdomain.entities.SchoolSupplyType
import com.intelligentbackpack.desktopdomain.entities.SchoolSupplyTypes
import com.intelligentbackpack.desktopdomain.entities.Subject

/**
 * Implementation of a book copy.
 *
 * @property rfidCode The RFID code of the book copy.
 * @property subjects The subjects of the book copy.
 * @property isbn The ISBN of the book copy.
 * @property title The title of the book copy.
 * @property authors The authors of the book copy.
 * @property replacedBy The book copies that replace this one.
 * @property replace The book copies that this one replaces.
 * @property type is automatically set to [SchoolSupplyTypes.BOOK].
 */
internal data class BookCopyImpl(
    override val rfidCode: String,
) : AbstractSchoolSupply<BookCopy>(), BookCopy {

    override lateinit var subjects: Set<Subject>
        private set
    override lateinit var isbn: String
        private set
    override lateinit var title: String
        private set
    override lateinit var authors: List<String>
        private set
    override var replacedBy: Set<BookCopy> = setOf()
        private set
    override var replace: Set<BookCopy> = setOf()
        private set

    constructor(
        rfidCode: String,
        subjects: Set<Subject>,
        isbn: String,
        title: String,
        authors: List<String>,
        replacedBy: Set<BookCopy>,
        replace: Set<BookCopy>
    ) : this(rfidCode) {
        this.subjects = subjects
        this.isbn = isbn
        this.title = title
        this.authors = authors
        this.replacedBy = replacedBy
        this.replace = replace
    }

    override val type: SchoolSupplyType = SchoolSupplyTypes.BOOK
    override fun copy(
        type: SchoolSupplyType,
        rfidCode: String,
        subjects: Set<Subject>,
        replacedBy: Set<BookCopy>,
        replace: Set<BookCopy>
    ): BookCopy =
        BookCopyImpl(
            rfidCode = rfidCode,
            subjects = subjects,
            isbn = isbn,
            title = title,
            authors = authors,
            replacedBy = replacedBy,
            replace = replace
        )
}
