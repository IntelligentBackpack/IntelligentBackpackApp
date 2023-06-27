package com.intelligentbackpack.desktopdomain.policies

/**
 * Object that contains the policy for the RFID.
 */
object RFIDPolicy {

    private val RFID_REGEX = Regex("([0-9A-F]{2}:)+[0-9A-F]{2}")

    /**
     * Checks whether the RFID is valid.
     * A RFID is valid if it is a string of hexadecimal numbers separated by colons.
     *
     * @param rfid the RFID to check.
     * @return true if the RFID is valid, false otherwise.
     *
     */
    fun isValid(rfid: String): Boolean {
        return RFID_REGEX.matches(rfid)
    }
}
