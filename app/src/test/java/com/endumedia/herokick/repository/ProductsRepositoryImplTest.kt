package com.endumedia.herokick.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.endumedia.herokick.api.FakeProductsApi
import com.endumedia.herokick.api.ItemsFactory
import com.endumedia.herokick.db.ProductsDao
import com.endumedia.herokick.db.ProductsDb
import com.endumedia.herokick.vo.Product
import junit.framework.Assert
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import java.util.concurrent.Executor

/**
 * Created by Nino on 01.10.19
 */
class ProductsRepositoryImplTest {


    @get:Rule // used to make all live data calls sync
    val instantExecutor = InstantTaskExecutorRule()

    private val db = Mockito.mock(ProductsDb::class.java)
    private val dao = Mockito.mock(ProductsDao::class.java)

    private val fakeApi = FakeProductsApi()
    private val itemsFactory = ItemsFactory()
    private val networkExecutor = Executor { command -> command.run() }

    private lateinit var repository: ProductsRepository
    private val productsLiveData = MutableLiveData<List<Product>>()
    private val FAILED_TO_CONNECT_MESSAGE = "Failed to connect to /192.168.0.138:8000"

    @Before
    fun init() {
        Mockito.`when`(db.productsDao()).thenReturn(dao)
//        Mockito.doReturn(getProductsDataSourceFromDb()).`when`(dao).getProducts()
//        Mockito.`when`(dao.deleteNotes()).then {
//            notesFactory.list.clear()
//        }
        repository = ProductsRepositoryImpl(db.productsDao(), fakeApi, networkExecutor)
    }



    /**
     * asserts that empty list works fine
     */
    @Test
    fun fetchEmptyList() {
        val listing = repository.getItems()
        val pagedList = getPagedList(listing)
        MatcherAssert.assertThat(pagedList.size, CoreMatchers.`is`(0))
    }

    /**
     * asserts that a list w/ single item is loaded properly
     */
    @Test
    fun oneItem() {
        val product = itemsFactory.createProduct()
        fakeApi.addProduct(product)
        val listing = repository.getItems()
        MatcherAssert.assertThat(getPagedList(listing), CoreMatchers.`is`(listOf(product)))
    }


    /**
     * asserts loading a page
     */
    @Test
    fun loadPage() {
        (0..10).map { itemsFactory.createProduct() }
        itemsFactory.list.forEach(fakeApi::addProduct)
        val listing = repository.getItems()
        // trigger loading of the whole list
        val pagedList = getPagedList(listing)
        pagedList.loadAllData()
        MatcherAssert.assertThat(pagedList, CoreMatchers.`is`(itemsFactory.list))
    }

    /**
     * asserts the failure message when the initial load cannot complete
     */
    @Test
    fun failToLoadInitial() {
        fakeApi.failureMsg = "xxx"
        val items = repository.getItems()
        // trigger load
        val listing = getPagedList(items)
        listing.loadAllData()
        Assert.assertTrue(listing.loadedCount == 0)
        MatcherAssert.assertThat(
            getNetworkState(items),
            CoreMatchers.`is`(NetworkState.error("xxx"))
        )
    }

    /**
     * asserts the retry logic when initial load request fails,
     * then load successfully
     */
    @Test
    fun retrySuccessAfterInitialFailedLoad() {
    }

    /**
     * asserts refresh clears old data and loads the new data in db
     */
    @Test
    fun refreshWithNonEmptyDb() {
    }


    /**
     * extract the latest paged list from the listing
     */
    private fun getPagedList(listing: Listing<Product>): PagedList<Product> {
        val observer = LoggingObserver<PagedList<Product>>()
        listing.pagedList.observeForever(observer)
        MatcherAssert.assertThat(observer.value, CoreMatchers.`is`(CoreMatchers.notNullValue()))
        return observer.value!!
    }


    private fun <T> PagedList<T>.loadAllData() {
        do {
            val oldSize = this.loadedCount
            this.loadAround(this.size - 1)
        } while (this.size != oldSize)
    }

    /**
     * extract the latest network state from the listing
     */
    private fun getNetworkState(listing: Listing<Product>) : NetworkState? {
        val networkObserver = LoggingObserver<NetworkState>()
        listing.networkState.observeForever(networkObserver)
        return networkObserver.value
    }

    /**
     * simple observer that logs the latest value it receives
     */
    private class LoggingObserver<T> : Observer<T> {
        var value : T? = null
        override fun onChanged(t: T?) {
            this.value = t
        }
    }

}