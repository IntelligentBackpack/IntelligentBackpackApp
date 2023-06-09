package com.intelligentbackpack.desktopdomain.policies

/**
 * Object that contains the ISBN policy.
 */
object ISBNPolicy {

    private val ISBN_REGEX = Regex("(97(8|9))\\d{10}")

    /**
     * Checks whether the ISBN is valid.
     * An ISBN is valid if it is a string of 13 digits that starts with 978 or 979 ends with a check digit.
     * The check digit is calculated as follows:
     * - Multiply each digit by 1 or 3 depending on the position of the digit (even or odd).
     * - Sum all the results.
     * - The check digit is the number that must be added to the sum to make it divisible by 10.
     *
     * @param isbn the ISBN to check.
     * @return true if the ISBN is valid, false otherwise.
     */
    fun isValid(isbn: String): Boolean {
        return isbn.length == 13 && ISBN_REGEX.matches(isbn) && isValidCheckDigit(isbn)
    }

    private fun isValidCheckDigit(isbn: String): Boolean {
        val sum = isbn.mapIndexed { index, c ->
            val digit = Character.getNumericValue(c)
            (if (index % 2 == 0) 1 else 3) * digit
        }.sum()
        return sum % 10 == 0
    }
}
