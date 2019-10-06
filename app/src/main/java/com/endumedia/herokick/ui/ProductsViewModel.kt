package com.endumedia.herokick.ui

import androidx.lifecycle.ViewModel
import com.endumedia.herokick.repository.ProductsRepository
import javax.inject.Inject


/**
 * Created by Nino on 01.10.19
 */
class ProductsViewModel @Inject
constructor(repository: ProductsRepository) : ViewModel() {

    private val listing = repository.getItems()

    val products = listing.pagedList

    val networkState = listing.networkState

    fun refresh() {
        listing.refresh.invoke()
    }

    fun retry() {
        listing.retry.invoke()
    }
}