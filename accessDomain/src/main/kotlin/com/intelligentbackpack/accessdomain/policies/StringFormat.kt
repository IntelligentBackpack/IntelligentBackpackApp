package com.intelligentbackpack.accessdomain.policies

import java.util.Locale

/**
 * String format policy.
 */
object StringFormat {

    /**
     * Convert the string to lower case and replace the first letter to upper case.
     *
     * @return the string with the first letter to upper case.
     */
    fun String.firstLetterToUpperCaseWithNoSpace(): String {
        return this.trim()
            .lowercase()
            .replaceFirstChar {
                if (it.isLowerCase()) {
                    it.titlecase(Locale.ROOT)
                } else {
                    it.toString()
                }
            }
    }
}
