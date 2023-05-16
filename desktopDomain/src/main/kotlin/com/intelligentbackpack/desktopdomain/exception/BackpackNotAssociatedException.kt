package com.intelligentbackpack.desktopdomain.exception

/**
 * Exception thrown when a backpack is not associated, but is used.
 */
class BackpackNotAssociatedException : IllegalStateException("The backpack is not associated.")
