package com.intelligentbackpack.accessdata.api

import access.communication.User
import access.communication.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT

interface AccessApi {

    @POST("/login")
    fun getUser(@Body user: User): Call<UserResponse>

    @PUT("/register")
    fun createNewUser(@Body user: User): Call<User>

    @HTTP(method = "DELETE", path = "/remove", hasBody = true)
    fun deleteUser(@Body user: User): Call<UserResponse>
}
