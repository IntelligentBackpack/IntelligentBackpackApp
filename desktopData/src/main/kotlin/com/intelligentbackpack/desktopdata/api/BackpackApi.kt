package com.intelligentbackpack.desktopdata.api

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * BackpackApi is the API interface for the backpack.
 */
interface BackpackApi {
    /**
     * Associates a backpack to an email.
     */
    @POST("/register/{hash}")
    fun associateBackpack(@Body email: RequestBody, @Path("hash") hash: String): Call<Void>

    /**
     * Disassociates a backpack from an email.
     */
    @HTTP(method = "DELETE", path = "/unregister/{hash}", hasBody = true)
    fun disassociateBackpack(@Body email: RequestBody, @Path("hash") hash: String): Call<Void>
}
