package com.intelligentbackpack.desktopdomain.entities

import java.util.Locale

/**
 * Type alias for a school supply type.
 */
typealias SchoolSupplyType = String

/**
 * Object that contains all the school supply types that are available by default (only Book).
 */
object SchoolSupplyTypes {

    /**
     * School supply type that refers to a book.
     */
    val BOOK = create("Book")

    /**
     * Creates a [SchoolSupplyType] from a [String].
     *
     * @param name The name of the [SchoolSupplyType].
     * @return The [SchoolSupplyType] created with lowercase letters and the first in upper case.
     */
    fun create(name: String): SchoolSupplyType =
        name.trim()
            .lowercase()
            .replaceFirstChar {
                it.titlecase(Locale.ROOT)
            }
}
