package com.intelligentbackpack.desktopdomain.entities

import com.intelligentbackpack.desktopdomain.entities.implementations.BookCopyImpl
import com.intelligentbackpack.desktopdomain.exception.ISBNException
import com.intelligentbackpack.desktopdomain.exception.RFIDFormatException
import com.intelligentbackpack.desktopdomain.exception.TypeException
import com.intelligentbackpack.desktopdomain.policies.ISBNPolicy
import com.intelligentbackpack.desktopdomain.policies.RFIDPolicy

/**
 * Interface for a book copy.
 */
interface BookCopy : SchoolSupply {
    /**
     * The ISBN of the book.
     */
    val isbn: String

    /**
     * The title of the book.
     */
    val title: String

    /**
     * The authors of the book.
     */
    val authors: List<String>

    companion object {

        /**
         * Builds a book copy.
         *
         * @param block The builder block.
         * @return The book copy built.
         * @throws IllegalArgumentException If the book copy is invalid
         * ( the [isbn] doesn't match with the [ISBNPolicy],
         * the [rfidCode] doesn't match with the [RFIDPolicy],
         * @throws IllegalStateException If not all the properties are initialized.
         */
        inline fun build(
            block: Builder.() -> Unit
        ): BookCopy = Builder().apply(block).build()
    }

    /**
     * Builder for a book copy.
     * The [type] is automatically set to [SchoolSupplyTypes.BOOK].
     *
     * @property checkSubjects The subjects that are available.
     *
     */
    class Builder {

        /**
         * The rfid of the school supplies.
         */
        lateinit var rfidCode: String

        /**
         * The ISBN of the book.
         */
        lateinit var isbn: String

        /**
         * The title of the book.
         */
        lateinit var title: String

        /**
         * The authors of the book.
         */
        lateinit var authors: List<String>

        /**
         * The type of the school supply.
         */
        var type: SchoolSupplyType = SchoolSupplyTypes.BOOK

        /**
         * Builds the school supply.
         *
         * @return The school supply built.
         * @throws IllegalStateException If not all properties are initialized.
         * @throws TypeException If the type of the school supply is not valid.
         * @throws RFIDFormatException If the RFID code of the school supply is not valid.
         * @throws IllegalArgumentException for any other reason.
         * @throws ISBNException If the ISBN of the book is not valid.
         */
        @Throws(
            IllegalStateException::class,
            TypeException::class,
            RFIDFormatException::class,
            IllegalArgumentException::class
        )
        fun build(): BookCopy =
            if (this::rfidCode.isInitialized &&
                this::isbn.isInitialized &&
                this::title.isInitialized &&
                this::authors.isInitialized
            )
                if (title.isNotBlank() &&
                    authors.isNotEmpty() &&
                    authors.all { it.isNotBlank() }
                )
                    if (type == SchoolSupplyTypes.BOOK)
                        if (RFIDPolicy.isValid(rfidCode))

                            if (ISBNPolicy.isValid(isbn)) {
                                BookCopyImpl(
                                    rfidCode = rfidCode,
                                    isbn = isbn,
                                    title = title,
                                    authors = authors
                                )
                            } else
                                throw ISBNException()
                        else
                            throw RFIDFormatException()
                    else
                        throw TypeException(type)
                else
                    throw IllegalStateException("Not all properties are initialized")
            else
                throw IllegalStateException("Not all properties are initialized")
    }
}
