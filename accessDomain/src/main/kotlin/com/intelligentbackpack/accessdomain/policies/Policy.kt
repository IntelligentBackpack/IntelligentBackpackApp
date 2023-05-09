package com.intelligentbackpack.accessdomain.policies

/**
 * Policy interface.
 * @param T is the type of the entity to check.
 */
interface Policy<T> {
    /**
     * Checks if the entity respects the policy.
     * @param entity is the entity to check.
     * @return true if the entity respects the policy, false otherwise.
     */
    fun isRespected(entity: T): Boolean
}
