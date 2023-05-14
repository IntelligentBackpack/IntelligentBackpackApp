package com.intelligentbackpack.accessdata.api

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PUT

interface LibraryApi {
    @PUT("/create/library")
    fun createLibrary(@Body params: RequestBody): Call<Void>
}
