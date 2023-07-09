package com.intelligentbackpack.desktopdomain.entities

import com.intelligentbackpack.desktopdomain.entities.implementations.BookCopyImpl
import com.intelligentbackpack.desktopdomain.exception.ISBNException
import com.intelligentbackpack.desktopdomain.exception.RFIDFormatException
import com.intelligentbackpack.desktopdomain.exception.TypeException
import com.intelligentbackpack.desktopdomain.policies.RFIDPolicy

/**
 * Interface for a book copy.
 */
interface BookCopy : SchoolSupply {
    val book: Book

    companion object {

        /**
         * Builds a book copy.
         *
         * @param block The builder block.
         * @return The book copy built.
         * @throws IllegalArgumentException If the book copy is invalid
         * the [rfidCode] doesn't match with the [RFIDPolicy],
         * @throws IllegalStateException If not all the properties are initialized.
         */
        inline fun build(
            block: Builder.() -> Unit,
        ): BookCopy = Builder().apply(block).build()
    }

    /**
     * Builder for a book copy.
     * The [type] is automatically set to [SchoolSupplyTypes.BOOK].
     *
     */
    class Builder {

        /**
         * The rfid of the school supplies.
         */
        lateinit var rfidCode: String

        /**
         * The book of the copy.
         */
        lateinit var book: Book

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
         * @throws ISBNException If the ISBN of the book is not valid.
         */
        @Throws(
            IllegalStateException::class,
            TypeException::class,
            RFIDFormatException::class,
        )
        fun build(): BookCopy =
            if (this::rfidCode.isInitialized &&
                this::book.isInitialized
            ) {
                if (type == SchoolSupplyTypes.BOOK) {
                    if (RFIDPolicy.isValid(rfidCode)) {
                        BookCopyImpl(
                            rfidCode = rfidCode,
                            book = book,
                        )
                    } else {
                        throw RFIDFormatException()
                    }
                } else {
                    throw TypeException(type)
                }
            } else {
                error("Not all properties are initialized")
            }
    }
}
