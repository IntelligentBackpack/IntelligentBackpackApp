package com.intelligentbackpack.desktopdomain.entities

import java.util.Locale

/**
 * Object that contains all the subjects that are available by default (only All).
 */
object Subjects {

    /**
     * Subject that refers to all the subjects.
     */
    const val ALL = "All"

    /**
     * Creates a subject.
     */
    fun create(subject: String): Subject =
        subject
            .trim()
            .lowercase()
            .replaceFirstChar {
                it.titlecase(Locale.ROOT)
            }
}

/**
 * Type alias for a subject.
 */
typealias Subject = String
