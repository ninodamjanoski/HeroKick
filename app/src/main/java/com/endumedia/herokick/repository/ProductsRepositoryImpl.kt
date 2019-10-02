package com.endumedia.herokick.repository

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.toLiveData
import com.endumedia.herokick.api.ProductsApi
import com.endumedia.herokick.db.ProductsDao
import com.endumedia.herokick.vo.Product
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor
import javax.inject.Inject


/**
 * Created by Nino on 01.10.19
 */
class ProductsRepositoryImpl @Inject
constructor(private val productsDao: ProductsDao,
            private val productsApi: ProductsApi,
            private val ioExecutor: Executor): ProductsRepository {


    private val networkState = MutableLiveData<NetworkState>()

    @MainThread
    private fun refreshNotes(): LiveData<NetworkState> {
        networkState.value = NetworkState.LOADING
        productsApi.getItems(1).enqueue(object : Callback<List<Product>> {
            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                // retrofit calls this on main thread so safe to call set value
                networkState.value = NetworkState.error(t.message)
            }

            override fun onResponse(
                call: Call<List<Product>>,
                response: Response<List<Product>>
            ) {
                ioExecutor.execute {
                    productsDao.deleteItems()
                    response.body()?.let { productsDao.insertItems(it) }
                    // since we are in bg thread now, post the result.
                    networkState.postValue(NetworkState.LOADED)
                }
            }
        })
        return networkState
    }
    override fun getItems(): Listing<Product> {

        // create a boundary callback which will observe when the user reaches to the edges of
        // the list and update the database with extra data.
        val boundaryCallback = ProductsBoundaryCallback(
            webservice = productsApi,
            handleResponse = {productsDao::insertItems},
            ioExecutor = ioExecutor
        )

        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            refreshNotes()
        }

        return Listing(
            pagedList = productsDao.getItems()
                .toLiveData(30,
                    boundaryCallback = boundaryCallback),
            networkState = boundaryCallback.networkState,
            retry = {
                boundaryCallback.helper.retryAllFailed()
            },
            refresh = {
                refreshTrigger.value = null
            },
            refreshState = refreshState
        )
    }
}