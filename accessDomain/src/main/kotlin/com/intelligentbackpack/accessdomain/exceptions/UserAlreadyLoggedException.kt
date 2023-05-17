package com.intelligentbackpack.accessdomain.exceptions

import com.intelligentbackpack.accessdomain.entities.User

/**
 * UserAlreadyLoggedException is the exception thrown when a user tries to log in but a user is already logged.
 */
class UserAlreadyLoggedException(val user: User) : IllegalStateException()
