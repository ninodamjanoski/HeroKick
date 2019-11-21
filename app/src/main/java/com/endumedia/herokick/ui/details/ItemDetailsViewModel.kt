package com.endumedia.herokick.ui.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.endumedia.herokick.repository.ProductsRepository
import com.endumedia.herokick.vo.Product
import javax.inject.Inject

class ItemDetailsViewModel @Inject
constructor(private val repository: ProductsRepository) : ViewModel() {

    private val idLiveData = MutableLiveData<String>()

    var itemLiveData = MutableLiveData<Product>()

    fun setCachedItem(item: Product) {
        if (item != itemLiveData.value) {
            itemLiveData.value = item
        }
    }

    fun getItemById(id: String) {
        itemLiveData = Transformations
            .switchMap(idLiveData) { id -> repository.getItemById(id)}
                as MutableLiveData<Product>
        idLiveData.value = id
    }
}
