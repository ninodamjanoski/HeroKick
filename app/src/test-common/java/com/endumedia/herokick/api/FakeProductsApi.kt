package com.endumedia.herokick.api

import com.endumedia.herokick.vo.Product
import retrofit2.Call
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

    override fun getItems(page: String): Call<List<Product>> {
        failureMsg?.let {
            return Calls.failure(IOException(it))
        }

        return Calls.response(model)
    }
}