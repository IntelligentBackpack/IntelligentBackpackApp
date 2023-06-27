package com.intelligentbackpack.app.ui.navigation

/**
 * object MainNavigation contains the navigation paths.
 */
object MainNavigation {

    /**
     * The login path.
     */
    const val login = "login"

    /**
     * The home path.
     */
    const val home = "home"

    /**
     * param email for the createUser path.
     */
    const val createUserParam = "email"

    /**
     * The createUser path with the param.
     */
    const val createUser = "createUser?$createUserParam={$createUserParam}"

    /**
     * The createUser path to navigate to, with the param if the email is not blank.
     *
     * @param email The email.
     * @return The path.
     */
    fun createUser(email: String) =
        if (email.isBlank()) {
            "createUser"
        } else {
            "createUser?$createUserParam=$email"
        }

    /**
     * The user path.
     */
    const val user = "user"

    /**
     * The schoolSupply param.
     */
    const val schoolSupplyParam = "rfid"

    /**
     * The schoolSupply path with the param.
     */
    const val schoolSupply = "schoolSupply?$schoolSupplyParam={$schoolSupplyParam}"

    /**
     * The schoolSupply path to navigate to, with the param if the rfid is not blank.
     *
     * @param rfid The rfid.
     * @return The path.
     */
    fun schoolSupply(rfid: String?) =
        rfid?.let {
            if (rfid.isBlank()) {
                "schoolSupply"
            } else {
                "schoolSupply?$schoolSupplyParam=$rfid"
            }
        } ?: "schoolSupply"

    /**
     * The event param.
     */
    const val eventParam = "index"

    /**
     * The event path with the param.
     */
    const val event = "event?$eventParam={$eventParam}"

    /**
     * The event path to navigate to, with the param if the index is not -1.
     *
     * @param index The index.
     * @return The path.
     */
    fun event(index: Int) =
        if (index == -1) {
            "event"
        } else {
            "event?$eventParam=$index"
        }
}
