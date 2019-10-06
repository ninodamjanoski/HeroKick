package com.endumedia.herokick.repository

import android.content.SharedPreferences
import android.text.TextUtils
import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import androidx.paging.toLiveData
import com.endumedia.herokick.api.ProductsApi
import com.endumedia.herokick.db.ProductsDao
import com.endumedia.herokick.vo.Product
import okhttp3.Headers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor
import java.util.regex.Pattern
import javax.inject.Inject


/**
 * Created by Nino on 01.10.19
 */
class ProductsRepositoryImpl @Inject
constructor(private val productsDao: ProductsDao,
            private val sharedPrefs: SharedPreferences,
            private val productsApi: ProductsApi,
            private val ioExecutor: Executor): ProductsRepository {

    /**
     * By refresh items, we want to empty items table and fetch as from the beginning
     * when the db is empty
     */
    private fun refreshItems() {
        // Deleting the items triggers on onZeroItemsLoaded in BoundaryCallback
        // which then calls the endpoint to fetch the first page
        ioExecutor.execute {
            productsDao.deleteItems()
        }
    }

    override fun getItems(): Listing<Product> {

        // create a boundary callback which will observe when the user reaches to the edges of
        // the list and update the database with extra data.
        val boundaryCallback = ProductsBoundaryCallback(
            webservice = productsApi,
            sharedPrefs = sharedPrefs,
            handleResponse = {insertItemsInDb(it)},
            ioExecutor = ioExecutor
        )

        return Listing(
            pagedList = productsDao.getItems()
                .toLiveData(10, boundaryCallback = boundaryCallback),
            networkState = boundaryCallback.networkState,
            retry = { boundaryCallback.helper.retryAllFailed() },
            refresh = { refreshItems() })
    }

    private fun insertItemsInDb(response: Response<List<Product>>) {

        val nextPage = getNextPage(response.headers())
        sharedPrefs.edit()
            .putInt(NEXT_PAGE, nextPage ?: -1)
            .apply()

        response.body()?.let {
            val filteredList = it.filter { item -> !TextUtils.isEmpty(item.id) &&
                    (!TextUtils.isEmpty(item.name) ||
                    !TextUtils.isEmpty(item.brandName) ||
                    !TextUtils.isEmpty(item.size))}
            productsDao.insertItems(filteredList)
        }
    }


    private fun getNextPage(headers: Headers): Int? {
        val pagingLinks = headers.values("link")
        return pagingLinks[0]
            .extractLinks()[NEXT_LINK]?.let { next ->
            val matcher = PAGE_PATTERN.matcher(next)
            if (!matcher.find() || matcher.groupCount() != 1) {
                null
            } else {
                try {
                    Integer.parseInt(matcher.group(1))
                } catch (ex: NumberFormatException) {
                    null
                }
            }
        }
    }

    companion object {
        private val LINK_PATTERN = Pattern.compile("<([^>]*)>[\\s]*;[\\s]*rel=\"([a-zA-Z0-9]+)\"")
        private val PAGE_PATTERN = Pattern.compile("\\bpage=(\\d+)")
        private const val NEXT_LINK = "next"

        private fun String.extractLinks(): Map<String, String> {
            val links = mutableMapOf<String, String>()
            val matcher = LINK_PATTERN.matcher(this)

            while (matcher.find()) {
                val count = matcher.groupCount()
                if (count == 2) {
                    links[matcher.group(2)] = matcher.group(1)
                }
            }
            return links
        }

        const val NEXT_PAGE = "nextPage"
    }
}