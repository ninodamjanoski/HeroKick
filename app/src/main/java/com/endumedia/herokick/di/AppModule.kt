package com.endumedia.herokick.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.room.Room
import com.endumedia.herokick.api.ProductsApi
import com.endumedia.herokick.db.ProductsDao
import com.endumedia.herokick.db.ProductsDb
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Singleton


/**
 * Created by Nino on 19.08.19
 */
@Module(includes = [ViewModelModule::class])
class AppModule {

    @Singleton
    @Provides
    fun provideDailyNotesApi(retrofit: Retrofit): ProductsApi {
        return retrofit.create(ProductsApi::class.java)
    }


    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        val baseUrl = "https://www.datakick.org/api/"
        val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
            Log.d("API", it)
        })
        logger.level = HttpLoggingInterceptor.Level.BASIC

        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }


    @Singleton
    @Provides
    fun provideSharedPrefs(app: Application): SharedPreferences {
        return app.getSharedPreferences("main", Context.MODE_PRIVATE)
    }
        @Singleton
    @Provides
    fun provideDb(app: Application): ProductsDb {
        return Room
            .databaseBuilder(app, ProductsDb::class.java, "products.db")
            .fallbackToDestructiveMigration()
            .build()
    }


    @Singleton
    @Provides
    fun provideProductsDao(db: ProductsDb): ProductsDao {
        return db.productsDao()
    }

    @Singleton
    @Provides
    fun provideIOAppExecutor(): Executor {
        return Executors.newFixedThreadPool(5)
    }
}