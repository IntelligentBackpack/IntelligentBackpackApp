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

    const val user = "user"

    const val schoolSupplyParam = "rfid"
    const val schoolSupply = "schoolSupply?$schoolSupplyParam={$schoolSupplyParam}"
    fun schoolSupply(rfid: String?) =
        rfid?.let {
            if (rfid.isBlank()) {
                "schoolSupply"
            } else {
                "schoolSupply?$schoolSupplyParam=$rfid"
            }
        } ?: "schoolSupply"
}
