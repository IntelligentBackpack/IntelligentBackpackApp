package com.intelligentbackpack.networkutility

import io.github.andreabrighi.converter.RetrofitJsonConverterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 * RetrofitHelper is an helper to create a Retrofit instance.
 */
object RetrofitHelper {
    /**
     * Creates a Retrofit instance.
     * @param baseUrl is the base url of the API.
     * @return the Retrofit instance.
     */
    fun getInstance(baseUrl: String): Retrofit {
        val httpClient = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
        return Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(RetrofitJsonConverterFactory.create())
            .client(httpClient.build())
            .build()
    }
}
