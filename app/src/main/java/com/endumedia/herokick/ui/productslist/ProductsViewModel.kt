package com.endumedia.herokick.ui.productslist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.endumedia.herokick.repository.ProductsRepository
import com.endumedia.herokick.repository.SortType
import javax.inject.Inject


class ProductsViewModel @Inject
constructor(repository: ProductsRepository) : ViewModel() {

    private val queryData = MutableLiveData<Pair<String, SortType>>().apply {
        value = Pair("", SortType.LATEST)
    }

    private val listing = Transformations.map(queryData) { sorting ->
        repository.getItems(sorting.first, sorting.second)
    }

    val products = Transformations.switchMap(listing) { it.pagedList }

    val networkState = Transformations.switchMap(listing) { it.networkState }

    val refreshState = Transformations.switchMap(listing) { it.refreshState }

    fun refresh() {
        listing.value?.refresh?.invoke()
    }

    fun retry() {
        listing.value?.retry?.invoke()
    }

    fun submitDataRequest(query: String, idxSorting: Int) {
        val sortType = SortType.values()[idxSorting]
        queryData.value = Pair(query, sortType)
    }
}