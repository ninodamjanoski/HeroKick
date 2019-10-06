package com.endumedia.herokick.db

import androidx.paging.DataSource
import androidx.room.*
import com.endumedia.herokick.repository.SortType
import com.endumedia.herokick.vo.Product


@Dao
abstract class ProductsDao {


    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertItems(list : List<Product>)

    fun getItems(sortType: SortType): DataSource.Factory<Int, Product> {
        return when (sortType) {
            SortType.LATEST -> getItems()
            SortType.NAME -> getItemsByName()
        }
    }

    @Query("SELECT * FROM Product")
    abstract fun getItems() : DataSource.Factory<Int, Product>


    @Query("SELECT * FROM Product ORDER BY name ASC")
    abstract fun getItemsByName() : DataSource.Factory<Int, Product>

    @Query("DELETE FROM Product")
    abstract fun deleteItems()
}
