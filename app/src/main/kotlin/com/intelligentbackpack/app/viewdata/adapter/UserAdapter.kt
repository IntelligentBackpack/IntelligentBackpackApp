package com.intelligentbackpack.app.viewdata.adapter

import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.app.view.toText
import com.intelligentbackpack.app.viewdata.UserView

object UserAdapter {

    fun UserView.fromViewToDomain() = User.build {
        this.surname = this@fromViewToDomain.surname
        this.name = this@fromViewToDomain.name
        this.email = this@fromViewToDomain.email
        this.password = this@fromViewToDomain.password
    }

    fun User.fromDomainToView() = UserView(
        email = email,
        password = password,
        name = name,
        surname = surname,
        role = role.toText()
    )
}