package com.intelligentbackpack.desktopdata.api

import book.communication.Book
import book.communication.BuyBook
import book.communication.Library
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

/**
 * Desktop is the API interface for the desktop data.
 *
 * Use retrofit annotations to define the HTTP request.
 */
interface DesktopApi {

    /**
     * Get the book data.
     *
     * @param isbn The ISBN of the book.
     * @return The book data.
     */
    @GET("/utility/getBook/")
    fun getBook(@Query("ISBN") isbn: String): Call<Book>

    /**
     * Get the desktop data.
     *
     * @param email The email of the user.
     * @return The desktop data.
     */
    @GET("/utility/getCopy/Email")
    fun getLibrary(@Query("email") email: String): Call<Library>

    /**
     * Add a book copy.
     *
     * @param buyBook The book copy to add.
     * @return The result of the operation.
     */
    @PUT("/create/buyBook")
    fun addBookCopy(@Body buyBook: BuyBook): Call<Void>

}
