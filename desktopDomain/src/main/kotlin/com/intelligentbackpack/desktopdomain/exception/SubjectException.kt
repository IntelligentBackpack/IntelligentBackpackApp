package com.intelligentbackpack.desktopdomain.exception

import com.intelligentbackpack.desktopdomain.entities.Subject

/**
 * Exception that is thrown when a subject is already present.
 * @param subjects The subjects that are already present.
 */
class SubjectException(val subjects: Set<Subject>) : IllegalArgumentException()
