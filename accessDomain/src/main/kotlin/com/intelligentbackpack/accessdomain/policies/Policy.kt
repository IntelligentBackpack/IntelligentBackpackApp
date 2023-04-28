package com.intelligentbackpack.accessdomain.policies

interface Policy<T> {
    fun isRespected(entity: T): Boolean
}
