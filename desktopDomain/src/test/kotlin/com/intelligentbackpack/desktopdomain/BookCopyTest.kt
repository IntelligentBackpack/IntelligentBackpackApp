package com.intelligentbackpack.desktopdomain

import com.intelligentbackpack.desktopdomain.entities.Book
import com.intelligentbackpack.desktopdomain.entities.BookCopy
import com.intelligentbackpack.desktopdomain.exception.ISBNException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class BookCopyTest : StringSpec({

    val title = "The Lord of the Rings"
    val authors = setOf("J. R. R. Tolkien")
    val rfidCode = "FF:24:3E:C1"
    val isbn = "9788843025343"
    val book = Book.build {
        this.isbn = isbn
        this.title = title
        this.authors = authors
    }

    "Create a book copy with a valid ISBN" {
        val bookCopy = BookCopy.build {
            this.rfidCode = rfidCode
            this.book = book
        }
        bookCopy.book.isbn shouldBe isbn
    }

    "Create a book copy with a valid RFID code" {
        val bookCopy = BookCopy.build {
            this.rfidCode = rfidCode
            this.book = book
        }
        bookCopy.rfidCode shouldBe rfidCode
    }

    "Create a book with a not valid ISBN missing a digit" {
        shouldThrow<ISBNException> {
            Book.build {
                this.isbn = "978885152159"
                this.title = title
                this.authors = authors
            }
        }
    }

    "Create a book with a not valid ISBN with a letter" {
        shouldThrow<ISBNException> {
            Book.build {
                this.isbn = "978885152159A"
                this.title = title
                this.authors = authors
            }
        }
    }

    "Create a book with a not valid ISBN with wrong check digit" {
        shouldThrow<ISBNException> {
            Book.build {
                this.isbn = "9788851521591"
                this.title = title
                this.authors = authors
            }
        }
    }

    "Create a book with an missing title" {
        shouldThrow<IllegalStateException> {
            Book.build {
                this.isbn = isbn
                this.authors = authors
            }
        }
    }

    "Create a book with an missing author" {
        shouldThrow<IllegalStateException> {
            Book.build {
                this.isbn = isbn
                this.title = title
            }
        }
    }

    "Create a book with an empty author" {
        val anonymousBook = Book.build {
            this.isbn = isbn
            this.title = title
            this.authors = setOf()
        }
        anonymousBook.authors shouldBe setOf()
    }

    "Create a book with an empty title" {
        shouldThrow<IllegalStateException> {
            Book.build {
                this.isbn = isbn
                this.title = ""
                this.authors = authors
            }
        }
    }
})
