package com.intelligentbackpack.desktopdomain.entities

import com.intelligentbackpack.desktopdomain.entities.implementations.BookCopyImpl
import com.intelligentbackpack.desktopdomain.exception.ISBNException
import com.intelligentbackpack.desktopdomain.policies.ISBNPolicy
import com.intelligentbackpack.desktopdomain.policies.RFIDPolicy
import com.intelligentbackpack.desktopdomain.policies.ReplacePolicy
import java.lang.IllegalArgumentException

/**
 * Interface for a book copy.
 */
interface BookCopy : MutableSchoolSupply<BookCopy> {
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
         * @param checkSubjects The subjects that are available.
         * @param block The builder block.
         * @return The book copy built.
         * @throws IllegalArgumentException If the book copy is invalid
         * ( the [isbn] doesn't match with the [ISBNPolicy],
         * the [rfidCode] doesn't match with the [RFIDPolicy],
         * the [subjects] aren't in the [checkSubjects],
         * [replace] or [replacedBy] doesn't respect the [ReplacePolicy]).
         * @throws IllegalStateException If not all the properties are initialized.
         */
        inline fun build(
            checkSubjects: Set<Subject>,
            block: Builder.() -> Unit
        ): BookCopy = Builder(checkSubjects).apply(block).build()
    }

    /**
     * Builder for a book copy.
     * The [type] is automatically set to [SchoolSupplyTypes.BOOK].
     *
     * @property checkSubjects The subjects that are available.
     *
     */
    class Builder(
        checkSubjects: Set<Subject>
    ) : SchoolSupplyBuilder<BookCopy>(listOf(SchoolSupplyTypes.BOOK), checkSubjects) {
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

        override fun build(): BookCopy {
            type = SchoolSupplyTypes.BOOK
            return super.build()
        }

        /**
         * Builds the book copy.
         *
         * @return The book copy built.
         * @throws IllegalArgumentException If the book copy is invalid
         * @throws ISBNException If the ISBN is not valid.
         * @throws IllegalStateException If not all the properties are initialized.
         */
        @Throws(IllegalArgumentException::class, IllegalStateException::class, ISBNException::class)
        override fun specificBuilder(): BookCopy =
            if (this::isbn.isInitialized &&
                this::title.isInitialized &&
                this::authors.isInitialized
            )
                if (title.isNotBlank() &&
                    authors.isNotEmpty() &&
                    authors.all { it.isNotBlank() }
                )
                    if (ISBNPolicy.isValid(isbn)) {

                        with(this) {
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
                    } else
                        throw ISBNException()
                else
                    throw IllegalArgumentException("Not all required fields are initialized")
            else
                throw IllegalStateException("Not all required fields are initialized")
    }
}
