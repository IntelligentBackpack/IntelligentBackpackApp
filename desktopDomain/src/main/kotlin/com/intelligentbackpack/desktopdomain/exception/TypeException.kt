package com.intelligentbackpack.desktopdomain.exception

import com.intelligentbackpack.desktopdomain.entities.SchoolSupplyType

/**
 * Exception that is thrown when a school supply type isn't present in the desktop.
 * @param type The type that is already present.
 */
class TypeException(val type: SchoolSupplyType) : IllegalArgumentException()
