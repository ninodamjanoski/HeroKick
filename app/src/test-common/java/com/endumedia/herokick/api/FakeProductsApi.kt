package com.endumedia.herokick.api

import com.endumedia.herokick.vo.Product
import okhttp3.Headers
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Query
import retrofit2.mock.Calls
import java.io.IOException


/**
 * Created by Nino on 02.10.19
 */
class FakeProductsApi: ProductsApi {

    private val model = mutableListOf<Product>()
    var failureMsg: String? = null

    fun addProduct(product: Product) {
        model.add(product)
    }

    fun clear() {
        failureMsg = null
        model.clear()
    }

    override fun getItems(@Query(value = "page") page: Int): Call<List<Product>> {
        failureMsg?.let {
            return Calls.failure(IOException(it))
        }

        val nextLink = "<https://www.datakick.org/api/items?page=${page + 1}>; rel=\"next\""
        val response = Response.success<List<Product>>(model, Headers.of("link", nextLink))
        return Calls.response(response)
    }
}