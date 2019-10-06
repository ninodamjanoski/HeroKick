package com.endumedia.herokick.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.endumedia.herokick.repository.ProductsRepository
import com.endumedia.herokick.repository.SortType
import javax.inject.Inject


/**
 * Created by Nino on 01.10.19
 */
class ProductsViewModel @Inject
constructor(repository: ProductsRepository) : ViewModel() {

    private val sortType = MutableLiveData<SortType>().apply {
        value = SortType.LATEST
    }

    private val listing = Transformations.map(sortType) { sorting ->
        repository.getItems(sorting)
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

    fun setSorting(idxSorting: Int) {
        val sorting = SortType.values()[idxSorting]
        if (sortType.value != sorting) {
            sortType.value = sorting
        }
    }
}