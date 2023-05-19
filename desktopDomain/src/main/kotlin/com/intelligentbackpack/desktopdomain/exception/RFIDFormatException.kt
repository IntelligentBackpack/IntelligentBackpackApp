package com.intelligentbackpack.desktopdomain.exception

/**
 * Exception that is thrown when a RFID code is not valid.
 */
class RFIDFormatException : IllegalArgumentException("The RFID code is not valid.")
