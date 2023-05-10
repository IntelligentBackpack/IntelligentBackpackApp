package com.intelligentbackpack.desktopdomain.entities

import com.intelligentbackpack.desktopdomain.entities.implementations.BackpackImpl

/**
 * Interface for a backpack.
 */
interface Backpack {
    /**
     * The name of the backpack.
     */
    val name: String

    companion object {
        /**
         * Creates a backpack.
         *
         * @param name The name of the backpack.
         * @return The backpack created.
         */
        fun create(name: String): Backpack = BackpackImpl(name)
    }
}
