package com.intelligentbackpack.accessdata.storage

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.intelligentbackpack.accessdomain.entities.Role
import com.intelligentbackpack.accessdomain.entities.User

/**
 * UserStorageImpl is the implementation of UserStorage.
 * @param context is the context of the application.
 *
 * This class uses SharedPreferences to save the user.
 */
class UserStorageImpl(private val context: Context) : UserStorage {

    private val name = "IntelligentBackpackSharedPref"

    override fun isUserSaved(): Boolean {
        val sp: SharedPreferences = context.getSharedPreferences(name, MODE_PRIVATE)
        return sp.contains("email")
    }

    /**
     * Saves the user.
     * @param user is the user to save.
     *
     * This function save the user password encrypted.
     */
    override fun saveUser(user: User) {
        val sp: SharedPreferences = context.getSharedPreferences(name, MODE_PRIVATE)
        val masterKey: MasterKey =
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
        val edit: SharedPreferences.Editor = sp.edit()
        edit.putString("email", user.email)
        edit.putString("name", user.name)
        edit.putString("surname", user.surname)
        edit.putString("role", user.role.name)
        edit.apply()
        val esp: SharedPreferences =
            EncryptedSharedPreferences.create(
                context,
                name,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )
        val encEdit: SharedPreferences.Editor = esp.edit()
        encEdit.putString("password", user.password)
        encEdit.apply()
    }

    override fun getUser(): User {
        check(isUserSaved()) { "User not saved" }
        val sp = context.getSharedPreferences(name, MODE_PRIVATE)

        val masterKey: MasterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        val esp: SharedPreferences = EncryptedSharedPreferences.create(
            context,
            name,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
        return User.build {
            email = sp.getString("email", "")!!
            name = sp.getString("name", "")!!
            surname = sp.getString("surname", "")!!
            password = esp.getString("password", "")!!
            role = Role.valueOf(sp.getString("role", "USER")!!)
        }
    }

    override fun deleteUser() {
        check(isUserSaved()) { "User not saved" }
        val sp: SharedPreferences =
            context.getSharedPreferences(name, MODE_PRIVATE)
        val edit: SharedPreferences.Editor = sp.edit()
        edit.clear()
        edit.apply()
    }
}
