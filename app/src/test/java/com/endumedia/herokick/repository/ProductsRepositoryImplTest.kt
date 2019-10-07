package com.endumedia.herokick.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.PositionalDataSource
import com.endumedia.herokick.api.FakeProductsApi
import com.endumedia.herokick.api.ItemsFactory
import com.endumedia.herokick.db.ProductsDao
import com.endumedia.herokick.db.ProductsDb
import com.endumedia.herokick.utils.MockedSharedPrefs
import com.endumedia.herokick.vo.Product
import junit.framework.Assert
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
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
    private var isDbEmpty: Boolean = false

    private var sortType = SortType.LATEST

    private lateinit var repository: ProductsRepository
    private val productsLiveData = MutableLiveData<List<Product>>()
    private val FAILED_TO_CONNECT_MESSAGE = "Failed to connect to /192.168.0.138:8000"
    private val fakeDatasource = getProductsDataSourceFromDb()

    @Before
    fun init() {
        Mockito.`when`(db.productsDao()).thenReturn(dao)
        Mockito.doReturn(fakeDatasource).`when`(dao).getItems(sortType)
        Mockito.`when`(dao.deleteItems()).then {
            itemsFactory.list.clear()
        }
        Mockito.`when`(dao.insertItems(itemsFactory.list)).then {
            isDbEmpty = false
            it
        }
        repository = ProductsRepositoryImpl(db.productsDao(), MockedSharedPrefs(),
            fakeApi, networkExecutor)
    }

    @After
    fun clear() {
        itemsFactory.list.clear()
        fakeApi.clear()
        isDbEmpty = false
    }

    /**
     * asserts that empty list works fine
     */
    @Test
    fun fetchEmptyList() {
        val listing = repository.getItems(sortType)
        val pagedList = getPagedList(listing)
        MatcherAssert.assertThat(pagedList.size, CoreMatchers.`is`(0))
    }

    /**
     * asserts that empty list works fine
     */
    @Test
    fun fetchEmptyListFromNetwork() {
        fetchFromNetwork()
    }

    /**
     * asserts loading a page
     */
    @Test
    fun loadPage() {
        (0..10).map { itemsFactory.createProduct() }
        itemsFactory.list.forEach(fakeApi::addProduct)
        val listing = repository.getItems(sortType)
        // trigger loading of the whole list
        val pagedList = getPagedList(listing)
        pagedList.loadAllData()
        MatcherAssert.assertThat(pagedList, CoreMatchers.`is`(itemsFactory.list))
    }

    /**
     * asserts that a list w/ single item is loaded properly from db
     */
    @Test
    fun oneItemFromDb() {
        val product = itemsFactory.createProduct()
        fakeApi.addProduct(product)
        val listing = repository.getItems(sortType)
        MatcherAssert.assertThat(getPagedList(listing), CoreMatchers.`is`(listOf(product)))
    }

    /**
     * asserts that a item is fetched from endpoint when db is empty
     */
    @Test
    fun oneItemOnEmptyDb() {
        val product = itemsFactory.createProduct()
        fakeApi.addProduct(product)

        fetchFromNetwork()
    }

    private fun fetchFromNetwork() {
        // Setting this to true, will force the boundary callback to call the
        // network endpoint
        isDbEmpty = true

        val listing = repository.getItems(sortType)

        val networkObserver = Mockito.mock(Observer::class.java) as Observer<NetworkState>
        listing.networkState.observeForever(networkObserver)

        val pagedList = getPagedList(listing)
        MatcherAssert.assertThat(pagedList, CoreMatchers.`is`(emptyList<Product>()))

        MatcherAssert.assertThat(getNetworkState(listing), CoreMatchers.`is`(NetworkState.LOADED))
        val inOrder = Mockito.inOrder(networkObserver)
        inOrder.verify(networkObserver).onChanged(NetworkState.LOADING)
        inOrder.verify(networkObserver).onChanged(NetworkState.LOADED)
        inOrder.verifyNoMoreInteractions()
    }

    /**
     * asserts refresh clears old data and loads the new data in db
     */
    @Test
    fun refreshWithNonEmptyDb() {

        itemsFactory.createProduct()

        val listing = repository.getItems(sortType)

        val pagedList = getPagedList(listing)
        MatcherAssert.assertThat(pagedList, CoreMatchers.`is`(itemsFactory.list))

        val networkObserver = Mockito.mock(Observer::class.java) as Observer<NetworkState>
        listing.refreshState.observeForever(networkObserver)

        val refreshProduct = itemsFactory.createProduct()
        fakeApi.addProduct(refreshProduct)

        listing.refresh.invoke()

        MatcherAssert.assertThat(getRefreshState(listing), CoreMatchers.`is`(NetworkState.LOADED))
        val inOrder = Mockito.inOrder(networkObserver)
        inOrder.verify(networkObserver).onChanged(NetworkState.LOADING)
        inOrder.verify(networkObserver).onChanged(NetworkState.LOADED)
        inOrder.verifyNoMoreInteractions()
    }

    /**
     * asserts the failure message when the initial load cannot complete
     */
    @Test
    fun failToLoadInitial() {
        failedToLoadFromNetwork()
    }

    private fun failedToLoadFromNetwork() {
        fakeApi.failureMsg = FAILED_TO_CONNECT_MESSAGE
        isDbEmpty = true
        val items = repository.getItems(sortType)
        // trigger load
        val listing = getPagedList(items)
        listing.loadAllData()
        Assert.assertTrue(listing.loadedCount == 0)
        MatcherAssert.assertThat(
            getNetworkState(items),
            CoreMatchers.`is`(NetworkState.error(fakeApi.failureMsg))
        )
    }

    /**
     * asserts the retry logic when initial load request fails,
     * then load successfully
     */
    @Test
    fun retrySuccessAfterInitialLoadFailed() {

        fakeApi.failureMsg = FAILED_TO_CONNECT_MESSAGE
        isDbEmpty = true

        val listing = repository.getItems(sortType)
        val pagedList = getPagedList(listing)
        pagedList.loadAllData()
        Assert.assertTrue(pagedList.loadedCount == 0)
        MatcherAssert.assertThat(getNetworkState(listing),
            CoreMatchers.`is`(NetworkState.error(fakeApi.failureMsg)))

        MatcherAssert.assertThat(pagedList, CoreMatchers.`is`(itemsFactory.list))

        val networkObserver = Mockito.mock(Observer::class.java) as Observer<NetworkState>
        listing.networkState.observeForever(networkObserver)

        val refreshProduct = itemsFactory.createProduct()
        fakeApi.clear()
        fakeApi.addProduct(refreshProduct)

        listing.retry.invoke()

        MatcherAssert.assertThat(getNetworkState(listing), CoreMatchers.`is`(NetworkState.LOADED))
        val inOrder = Mockito.inOrder(networkObserver)
        inOrder.verify(networkObserver).onChanged(NetworkState.LOADING)
        inOrder.verify(networkObserver).onChanged(NetworkState.LOADED)
        inOrder.verifyNoMoreInteractions()
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
        var oldSize = this.loadedCount
        while (this.size != oldSize) {
            oldSize = this.loadedCount
            this.loadAround(this.size - 1)
        }
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
     * extract the latest network state from the listing
     */
    private fun getRefreshState(listing: Listing<Product>) : NetworkState? {
        val networkObserver = LoggingObserver<NetworkState>()
        listing.refreshState.observeForever(networkObserver)
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


    private fun getProductsDataSourceFromDb(): DataSource.Factory<Int, Product> {
        return object : DataSource.Factory<Int, Product>() {
            override fun create(): DataSource<Int, Product> {
                return object : PositionalDataSource<Product>() {
                    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Product>) {
                        if (isDbEmpty) {
                            callback.onResult(mutableListOf())
                        } else {
                            callback.onResult(itemsFactory.list)
                        }
                    }

                    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Product>) {
                        if (isDbEmpty) {
                            callback.onResult(mutableListOf(), 0, 0)
                        } else {
                            callback.onResult(itemsFactory.list, 0, itemsFactory.list.size)
                        }
                    }
                }
            }
        }
    }
}