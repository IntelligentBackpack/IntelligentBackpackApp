package com.intelligentbackpack.desktopdomain.entities

import com.intelligentbackpack.desktopdomain.entities.implementations.BookImpl
import com.intelligentbackpack.desktopdomain.exception.ISBNException
import com.intelligentbackpack.desktopdomain.policies.ISBNPolicy

interface Book {
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
    val authors: Set<String>

    companion object {

        /**
         * Builds a book copy.
         *
         * @param block The builder block.
         * @return The book copy built.
         * @throws IllegalArgumentException If the book title is black or the authors are blanck.
         * @throws ISBNException If the ISBN of the book is not valid.
         * @throws IllegalStateException If not all the properties are initialized.
         */
        inline fun build(
            block: Builder.() -> Unit
        ): Book = Builder().apply(block).build()
    }

    class Builder {

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
        lateinit var authors: Set<String>

        /**
         * Builds the school supply.
         *
         * @return The school supply built.
         * @throws IllegalStateException If not all properties are initialized.
         * @throws IllegalArgumentException for any other reason.
         * @throws ISBNException If the ISBN of the book is not valid.
         */
        @Throws(
            IllegalStateException::class,
            ISBNException::class,
            IllegalArgumentException::class
        )
        fun build(): Book =
            if (this::isbn.isInitialized &&
                this::title.isInitialized &&
                this::authors.isInitialized
            )
                if (title.isNotBlank() &&
                    authors.all { it.isNotBlank() }
                )
                    if (ISBNPolicy.isValid(isbn)) {
                        BookImpl(
                            isbn = isbn,
                            title = title,
                            authors = authors
                        )
                    } else
                        throw ISBNException()
                else
                    throw IllegalStateException("Not all properties are initialized")
            else
                throw IllegalStateException("Not all properties are initialized")
    }
}