package com.intelligentbackpack.accessdomain.exceptions

/**
 * UserAlreadyLogged is the exception thrown when a user tries to log in but a user is already logged.
 */
class UserAlreadyLogged(val email: String) : IllegalStateException()
