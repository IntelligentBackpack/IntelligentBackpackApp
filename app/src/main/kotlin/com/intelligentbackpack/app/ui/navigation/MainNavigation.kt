package com.intelligentbackpack.app.ui.navigation

object MainNavigation {
    const val login = "login"
    const val home = "home"
    const val createUserParam = "email"
    const val createUser = "createUser?$createUserParam={$createUserParam}"
    fun createUser(email: String) =
        if (email.isBlank()) {
            "createUser"
        } else {
            "createUser?$createUserParam=$email"
        }
}