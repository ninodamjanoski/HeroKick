package com.endumedia.herokick.repository

import androidx.lifecycle.LiveData
import com.endumedia.herokick.vo.Product


/**
 * Created by Nino on 01.10.19
 */
interface ProductsRepository {
    fun getItems(query: String, sortType: SortType): Listing<Product>
    fun getItemById(id: String): LiveData<Product>
}