package com.endumedia.herokick.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.room.Room
import com.endumedia.herokick.BuildConfig
import com.endumedia.herokick.api.ProductsApi
import com.endumedia.herokick.db.ProductsDao
import com.endumedia.herokick.db.ProductsDb
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.security.ProviderInstaller
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
import javax.net.ssl.SSLContext


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
    fun provideRetrofit(app: Application): Retrofit {
        val baseUrl = "https://www.datakick.org/api/"
        val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
            Log.d("API", it)
        })
        logger.level = HttpLoggingInterceptor.Level.BASIC

        // Enabling TLSv1.2 on pre 20 Apis
        enableTLSv12OnPre20Apis(app)

        // Enabled
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

    private fun enableTLSv12OnPre20Apis(app: Application) {
        try {
            ProviderInstaller.installIfNeeded(app);
            val sslContext = SSLContext.getInstance("TLSv1.2")
            sslContext.init(null, null, null)
            sslContext.createSSLEngine()
        } catch (e: GooglePlayServicesNotAvailableException) {
            if (BuildConfig.DEBUG) e.printStackTrace()
        }
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