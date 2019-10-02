package com.endumedia.herokick.db

import androidx.paging.DataSource
import androidx.room.*
import com.endumedia.herokick.vo.Product


@Dao
interface ProductsDao {


    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItems(list : List<Product>)


    @Query("SELECT * FROM Product")
    fun getItems() : DataSource.Factory<Int, Product>


    @Query("DELETE FROM Product")
    fun deleteItems()
}
