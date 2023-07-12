package com.intelligentbackpack.app.viewdata

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * View data class for a user.
 *
 * @property email the email of the user.
 * @property password the password of the user.
 * @property name the name of the user.
 * @property surname the surname of the user.
 * @property role the role of the user.
 */
@Parcelize
data class UserView(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val surname: String = "",
    val role: String = "",
) : Parcelable
