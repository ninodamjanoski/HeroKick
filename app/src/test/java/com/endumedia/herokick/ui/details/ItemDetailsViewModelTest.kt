package com.endumedia.herokick.ui.details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.endumedia.herokick.api.ItemsFactory
import com.endumedia.herokick.repository.ProductsRepository
import com.endumedia.herokick.vo.Product
import com.endumedia.notes.repository.utils.mock
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

/**
 * Created by Nino on 21.11.19
 */
class ItemDetailsViewModelTest {


    @get:Rule // used to make all live data calls sync
    val instantExecutor = InstantTaskExecutorRule()
    private val itemsFactory = ItemsFactory()

    private val repository: ProductsRepository = mock()
    private lateinit var model: ItemDetailsViewModel

    private val sortTypeObserver: Observer<Product> = mock()

    @Before
    fun setUp() {
        model = ItemDetailsViewModel(repository)
    }

    @After
    fun Clean() {
        itemsFactory.list.clear()
    }

    @Test
    fun setCachedItem() {
        model.itemLiveData.observeForever(sortTypeObserver)
        model.setCachedItem(itemsFactory.createProduct())

        Mockito.verify(sortTypeObserver)
            .onChanged(itemsFactory.list.last())
    }


}