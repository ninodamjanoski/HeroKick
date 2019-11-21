package com.endumedia.herokick.api

import com.endumedia.herokick.vo.Product
import java.util.concurrent.atomic.AtomicLong


/**
 * Created by Nino on 02.10.19
 */
class ItemsFactory {

    private val counter = AtomicLong(0)
    val list = mutableListOf<Product>()

    fun createProduct() : Product {
        val id = counter.incrementAndGet()
        val product = Product("$id", "brand $id",
            "name $id","$id g",
            "name $id","$id g",
            "name $id","$id g",
            "name $id","$id g", "name $id")
        list.add(product)
        return product
    }
}