package com.intelligentbackpack.desktopdata.storage

/**
 * Shared preferences storage for desktop module
 */
interface DesktopStorage {

    /**
     * Save backpack hash
     */
    fun isBackpackSaved(): Boolean

    /**
     * Save backpack hash
     *
     * @param hash backpack hash
     */
    fun saveBackpack(hash: String)

    /**
     * Get backpack hash
     */
    fun getBackpack(): String

    /**
     * Delete backpack hash
     */
    fun deleteBackpack()
}