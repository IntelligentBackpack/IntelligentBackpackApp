package com.intelligentbackpack.desktopdomain.policies

/**
 * Object that contains the ISBN policy.
 */
object ISBNPolicy {

    private val ISBN_REGEX = Regex("(97(8|9))\\d{9}(\\d|X)")

    /**
     * Checks whether the ISBN is valid.
     * An ISBN is valid if it is a string of 13 digits that starts with 978 or 979 ends with a check digit.
     * The check digit is calculated as follows:
     * - multiply each digit by 10 minus its position in the string (skipping the first 3 digits) from right to left;
     * - sum all the results;
     * - the check digit is the number that must be added to the sum to make it divisible by 11.
     *
     * @param isbn the ISBN to check.
     * @return true if the ISBN is valid, false otherwise.
     */
    fun isValid(isbn: String): Boolean {
        return isbn.length == 13 && ISBN_REGEX.matches(isbn) && isValidCheckDigit(isbn)
    }

    private fun isValidCheckDigit(isbn: String): Boolean {
        val checkDigit = isbn[isbn.length - 1].toString()
        val isbnWithoutCheckDigit = isbn.substring(3, isbn.length - 1)
        val checkDigitCalculated = calculateCheckDigit(isbnWithoutCheckDigit)
        return checkDigit == checkDigitCalculated
    }

    private fun calculateCheckDigit(isbnWithoutCheckDigit: String): String {
        val sum = isbnWithoutCheckDigit.mapIndexed { index, c ->
            val digit = Character.getNumericValue(c)
            (10 - index) * digit
        }.sum()
        val check = sum % 11
        return if (check == 0) "0" else if (11 - check == 10) "X" else (11 - check).toString()
    }
}
