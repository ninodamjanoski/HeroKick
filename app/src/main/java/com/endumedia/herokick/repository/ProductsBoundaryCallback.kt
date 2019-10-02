package com.endumedia.herokick.repository

import androidx.annotation.MainThread
import androidx.paging.PagedList
import androidx.paging.PagingRequestHelper
import com.endumedia.herokick.api.ProductsApi
import com.endumedia.herokick.vo.Product
import com.endumedia.notes.util.createStatusLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor


/**
 * Created by Nino on 02.10.19
 */
class ProductsBoundaryCallback(private val webservice: ProductsApi,
                               private val handleResponse: (List<Product>?) -> Unit,
                               private val ioExecutor: Executor,
                               private val networkPageSize: Int = 10)
    : PagedList.BoundaryCallback<Product>()  {

    val helper = PagingRequestHelper(ioExecutor)
    val networkState = helper.createStatusLiveData()


    /**
     * Database returned 0 items. We should query the backend for more items.
     */
    @MainThread
    override fun onZeroItemsLoaded() {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            webservice.getItems(1)
                .enqueue(createWebserviceCallback(it))
        }
    }



    /**
     * User reached to the end of the list.
     * TODO to be implemented when theres paging supported by the backend service
     */
    @MainThread
    override fun onItemAtEndLoaded(itemAtEnd: Product) {
        super.onItemAtEndLoaded(itemAtEnd)
        helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
            webservice.getItems(1)
                .enqueue(createWebserviceCallback(it))
        }
    }

    private fun createWebserviceCallback(it: PagingRequestHelper.Request.Callback)
            : Callback<List<Product>> {

        return object : Callback<List<Product>> {
            override fun onFailure(
                call: Call<List<Product>>,
                t: Throwable) {
                it.recordFailure(t)
            }

            override fun onResponse(
                call: Call<List<Product>>,
                response: Response<List<Product>>) {
                insertItemsIntoDb(response, it)
            }
        }
    }

    /**
     * every time it gets new items, boundary callback simply inserts them into the database and
     * paging library takes care of refreshing the list if necessary.
     */
    private fun insertItemsIntoDb(response: Response<List<Product>>,
                                  it: PagingRequestHelper.Request.Callback) {
        ioExecutor.execute {
            handleResponse(response.body())
            it.recordSuccess()
        }
    }

}