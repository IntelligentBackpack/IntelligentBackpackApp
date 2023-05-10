package com.intelligentbackpack.desktopdomain.entities

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
    fun create(subject: Subject): Subject = subject
}

/**
 * Type alias for a subject.
 */
typealias Subject = String
