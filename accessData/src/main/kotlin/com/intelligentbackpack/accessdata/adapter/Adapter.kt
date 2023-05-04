package com.intelligentbackpack.accessdata.adapter

import com.intelligentbackpack.accessdomain.entities.Role
import com.intelligentbackpack.accessdomain.entities.User
import access.communication.User as UserRemote
import access.communication.Role as RoleRemote
import access.communication.user as userRemote

object Adapter {

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