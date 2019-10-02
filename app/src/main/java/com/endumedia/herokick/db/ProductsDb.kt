package com.endumedia.herokick.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.endumedia.herokick.vo.Product


/**
 * Created by Nino on 01.10.19
 */
@Database(
    entities = arrayOf(Product::class),
    version = 1,
    exportSchema = false)
abstract class ProductsDb : RoomDatabase() {

    companion object {
        fun create(context: Context, useInMemory : Boolean): ProductsDb {
            val databaseBuilder = if(useInMemory) {
                Room.inMemoryDatabaseBuilder(context, ProductsDb::class.java)
            } else {
                Room.databaseBuilder(context, ProductsDb::class.java, "products.db")
            }
            return databaseBuilder
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    abstract fun productsDao(): ProductsDao

}