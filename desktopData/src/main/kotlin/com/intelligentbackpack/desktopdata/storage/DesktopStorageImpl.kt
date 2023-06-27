package com.intelligentbackpack.desktopdata.storage

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

/**
 * Shared preferences storage for desktop module
 */
class DesktopStorageImpl(private val context: Context) : DesktopStorage {

    private val name = "IntelligentBackpackDesktopSharedPref"

    override fun isBackpackSaved(): Boolean {
        val sp: SharedPreferences = context.getSharedPreferences(name, MODE_PRIVATE)
        return sp.contains("backpack")
    }

    override fun saveBackpack(hash: String) {
        val sp: SharedPreferences = context.getSharedPreferences(name, MODE_PRIVATE)
        val edit: SharedPreferences.Editor = sp.edit()
        edit.putString("backpack", hash)
        edit.apply()
    }

    /**
     * Get backpack hash
     *
     * @throws IllegalStateException if backpack is not saved
     */
    @Throws(IllegalStateException::class)
    override fun getBackpack(): String {
        if (!isBackpackSaved()) {
            throw IllegalStateException("Backpack not saved")
        }
        val sp = context.getSharedPreferences(name, MODE_PRIVATE)
        return sp.getString("backpack", "")!!
    }

    /**
     * Delete backpack hash
     *
     * @throws IllegalStateException if backpack is not saved
     */
    override fun deleteBackpack() {
        if (!isBackpackSaved()) {
            throw IllegalStateException("Backpack not saved")
        }
        val sp: SharedPreferences = context.getSharedPreferences(name, MODE_PRIVATE)
        val edit: SharedPreferences.Editor = sp.edit()
        edit.remove("backpack")
        edit.apply()
    }
}
