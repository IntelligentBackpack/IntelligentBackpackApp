package com.intelligentbackpack.desktopdata.api

import book.communication.Book
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Desktop is the API interface for the desktop data.
 *
 * Use retrofit annotations to define the HTTP request.
 */
interface DesktopApi {

    @GET("/utility/getBook/")
    fun getBook(@Query("isbn") isbn: String): Call<Book>

}
