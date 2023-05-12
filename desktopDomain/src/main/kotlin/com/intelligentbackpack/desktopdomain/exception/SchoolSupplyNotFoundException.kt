package com.intelligentbackpack.desktopdomain.exception

/**
 * Exception that is thrown when a school supply is not found.
 *
 * @param rfid The RFID code of the school supply that is not found.
 */
class SchoolSupplyNotFoundException(val rfid: String) : IllegalArgumentException()
