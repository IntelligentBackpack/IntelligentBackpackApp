package com.intelligentbackpack.app.viewdata

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * View data class for a user.
 */
@Parcelize
data class UserView(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val surname: String = "",
    val role: String = ""
) : Parcelable
