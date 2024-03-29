package com.endumedia.herokick.api

import com.endumedia.herokick.vo.Product
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


/**
 * Created by Nino on 01.10.19
 */

interface ProductsApi {

    @GET("items")
    fun getItems(@Query("page") page: Int = 1): Call<List<Product>>

    @GET("items/{id}")
    fun getById(@Path("id") id: String): Call<Product>
}