package com.endumedia.herokick.repository

import com.endumedia.herokick.vo.Product


/**
 * Created by Nino on 01.10.19
 */
interface ProductsRepository {
    fun getItems(sortType: SortType): Listing<Product>
}