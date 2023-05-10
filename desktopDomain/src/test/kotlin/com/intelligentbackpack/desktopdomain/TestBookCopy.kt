package com.intelligentbackpack.desktopdomain

import com.intelligentbackpack.desktopdomain.entities.BookCopy
import com.intelligentbackpack.desktopdomain.exception.ISBNException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TestBookCopy : StringSpec({

    val title = "The Lord of the Rings"
    val authors = listOf("J. R. R. Tolkien")
    val subjects = setOf("English")
    val rfidCode = "FF:24:3E:C1"
    val isbn = "978885152159X"

    "Create a book copy with a valid ISBN" {
        val bookCopy = BookCopy.build(subjects) {
            this.isbn = isbn
            this.rfidCode = rfidCode
            this.subjects = subjects
            this.title = title
            this.authors = authors
        }
        bookCopy.isbn shouldBe isbn
    }

    "Create a book copy with a valid RFID code" {
        val bookCopy = BookCopy.build(subjects) {
            this.isbn = isbn
            this.rfidCode = rfidCode
            this.subjects = subjects
            this.title = title
            this.authors = authors
        }
        bookCopy.rfidCode shouldBe rfidCode
    }

    "Create a book with a not valid ISBN missing a digit" {
        shouldThrow<ISBNException> {
            BookCopy.build(subjects) {
                this.isbn = "978885152159"
                this.rfidCode = rfidCode
                this.subjects = subjects
                this.title = title
                this.authors = authors
            }
        }
    }

    "Create a book with a not valid ISBN with a letter, not X at the end" {
        shouldThrow<ISBNException> {
            BookCopy.build(subjects) {
                this.isbn = "9788851521599"
                this.rfidCode = rfidCode
                this.subjects = subjects
                this.title = title
                this.authors = authors
            }
        }
    }

    "Create a book with a not valid ISBN with wrong check digit" {
        shouldThrow<ISBNException> {
            BookCopy.build(subjects) {
                this.isbn = "97888515215A0"
                this.rfidCode = rfidCode
                this.subjects = subjects
                this.title = title
                this.authors = authors
            }
        }
    }

    "Create a book with an missing title" {
        shouldThrow<IllegalStateException> {
            BookCopy.build(subjects) {
                this.isbn = isbn
                this.rfidCode = rfidCode
                this.subjects = subjects
                this.authors = authors
            }
        }
    }

    "Create a book with an missing author" {
        shouldThrow<IllegalStateException> {
            BookCopy.build(subjects) {
                this.isbn = isbn
                this.rfidCode = rfidCode
                this.subjects = subjects
                this.title = title
            }
        }
    }

    "Create a book with an missing subject" {
        shouldThrow<IllegalStateException> {
            BookCopy.build(subjects) {
                this.isbn = isbn
                this.rfidCode = rfidCode
                this.title = title
                this.authors = authors
            }
        }
    }

    "Create a book with an empty title" {
        shouldThrow<IllegalStateException> {
            BookCopy.build(setOf("English")) {
                this.isbn = isbn
                this.rfidCode = rfidCode
                this.subjects = subjects
                this.authors = authors
            }
        }
    }
})
