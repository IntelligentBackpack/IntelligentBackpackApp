package com.intelligentbackpack.desktopdata.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * BackpackApi is the API interface for the backpack.
 */
interface BackpackApi {
    /**
     * Associates a backpack to an email.
     */
    @GET("/register")
    fun associateBackpack(@Query("email") email: String, @Query("hash") hash: String): Call<Void>
}