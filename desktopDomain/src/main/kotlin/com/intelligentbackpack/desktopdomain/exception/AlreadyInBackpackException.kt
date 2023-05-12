package com.intelligentbackpack.desktopdomain.exception

import com.intelligentbackpack.desktopdomain.entities.SchoolSupply

/**
 * Exception that is thrown when a school supply is already in the backpack.
 *
 * @param schoolSupply The school supply that is already in the backpack.
 */
class AlreadyInBackpackException(val schoolSupply: SchoolSupply) : IllegalArgumentException()
