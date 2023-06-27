package com.intelligentbackpack.accessdata.api

import access.communication.User
import access.communication.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT

/**
 * AccessApi is the API interface for the access data.
 *
 * Use retrofit annotations to define the HTTP request.
 */
interface AccessApi {

    /**
     * Logs a user.
     * @param user is the user to log.
     * @return the [Call] with the logged user response or error.
     */
    @POST("/login")
    fun getUser(@Body user: User): Call<UserResponse>

    /**
     * Creates a new user.
     * @param user is the user to create.
     * @return the [Call] with the created user or error.
     */
    @PUT("/register")
    fun createNewUser(@Body user: User): Call<User>

    /**
     * Deletes a user.
     * @param user is the user to delete.
     * @return the [Call] with the deleted user response or error.
     */
    @HTTP(method = "DELETE", path = "/remove", hasBody = true)
    fun deleteUser(@Body user: User): Call<UserResponse>
}
