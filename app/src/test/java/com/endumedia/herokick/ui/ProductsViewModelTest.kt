package com.endumedia.herokick.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.endumedia.herokick.api.ItemsFactory
import com.endumedia.herokick.repository.Listing
import com.endumedia.herokick.repository.NetworkState
import com.endumedia.herokick.repository.ProductsRepository
import com.endumedia.herokick.repository.SortType
import com.endumedia.herokick.ui.productslist.ProductsViewModel
import com.endumedia.herokick.vo.Product
import com.endumedia.notes.repository.utils.mock
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.times

/**
 * Created by Nino on 20.11.19
 */
class ProductsViewModelTest {


    @get:Rule // used to make all live data calls sync
    val instantExecutor = InstantTaskExecutorRule()

    private val itemsFactory = ItemsFactory()

    private val repository: ProductsRepository = mock()
    private lateinit var model: ProductsViewModel

    private val pagedListData = MutableLiveData<PagedList<Product>>()

    private val networkState: LiveData<NetworkState> = mock()
    private val refreshState: LiveData<NetworkState> = mock()

    private val productsObserver: Observer<PagedList<Product>> = mock()

    private val sortType = SortType.LATEST
    private val query = ""

    @Before
    fun setUp() {
        model = ProductsViewModel(repository)
        Mockito.`when`(repository.getItems(query, sortType))
            .thenReturn(Listing(pagedListData,
                networkState, refreshState, {}, {}))
    }

    @After
    fun Clean() {
        itemsFactory.list.clear()
    }

    @Test
    fun submitDataRequestRepoGetItemsCall() {
        model.products.observeForever(productsObserver)
        model.submitDataRequest(query, SortType.LATEST.ordinal)
        // First time on init, and second time in submit
        Mockito.verify(repository, times(2)).getItems(query, sortType)
    }
}