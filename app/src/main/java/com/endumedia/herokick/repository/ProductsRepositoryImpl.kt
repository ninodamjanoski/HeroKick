package com.endumedia.herokick.repository

import com.endumedia.herokick.api.ProductsApi
import com.endumedia.herokick.db.ProductsDao
import com.endumedia.herokick.vo.Product
import java.util.concurrent.Executor
import javax.inject.Inject


/**
 * Created by Nino on 01.10.19
 */
class ProductsRepositoryImpl @Inject
constructor(productsDao: ProductsDao,
            productsApi: ProductsApi,
            private val ioExecutor: Executor): ProductsRepository {

    override fun getItems(): Listing<Product> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}