package com.intelligentbackpack.desktopdomain.entities.implementations

import com.intelligentbackpack.desktopdomain.entities.Backpack

/**
 * Implementation of a backpack.
 *
 * @property name The name of the backpack.
 */
internal data class BackpackImpl(override val name: String) : Backpack
