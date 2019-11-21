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

    fun getItems(query: String, sortType: SortType): DataSource.Factory<Int, Product> {
        return when (sortType) {
            SortType.LATEST -> getItems(query)
            SortType.NAME -> getItemsByName(query)
        }
    }

    @Query("SELECT * FROM Product WHERE name LIKE '%'|| :keyWord ||'%'")
    abstract fun getItems(keyWord: String) : DataSource.Factory<Int, Product>


    @Query("SELECT * FROM Product WHERE name LIKE '%'|| :keyWord ||'%' ORDER BY name ASC")
    abstract fun getItemsByName(keyWord: String) : DataSource.Factory<Int, Product>

    @Query("DELETE FROM Product")
    abstract fun deleteItems()
}
