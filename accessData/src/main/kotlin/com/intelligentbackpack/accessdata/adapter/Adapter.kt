package com.intelligentbackpack.accessdata.adapter

import com.intelligentbackpack.accessdomain.entities.Role
import com.intelligentbackpack.accessdomain.entities.User
import access.communication.User as UserRemote
import access.communication.Role as RoleRemote
import access.communication.user as userRemote

/**
 * Adapter is used to convert data from domain to remote and vice versa.
 */
object Adapter {

    /**
     * Converts a user from domain to remote.
     * @return the converted remote user.
     */
    fun User.fromDomainToRemote(): UserRemote {
        val user = this
        return userRemote {
            email = user.email
            password = user.password
            nome = user.name
            cognome = user.surname
            role = when (user.role) {
                Role.USER -> RoleRemote.USER
                Role.TEACHER -> RoleRemote.TEACHER
                Role.STUDENT -> RoleRemote.STUDENT
                else -> {
                    throw IllegalArgumentException("Role not valid")
                }
            }
        }
    }

    /**
     * Converts a user from remote to domain.
     * @return the converted domain user.
     */
    fun UserRemote.fromRemoteToDomain(): User {
        val user = this
        return User.build {
            email = user.email
            password = user.password
            name = user.nome
            surname = user.cognome
            role = when (user.role) {
                RoleRemote.USER -> Role.USER
                RoleRemote.TEACHER -> Role.TEACHER
                RoleRemote.STUDENT -> Role.STUDENT
                else -> {
                    throw IllegalArgumentException("Role not valid")
                }
            }
        }
    }
}