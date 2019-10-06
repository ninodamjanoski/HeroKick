package com.endumedia.herokick.di

import com.endumedia.herokick.repository.ProductsRepository
import com.endumedia.herokick.repository.ProductsRepositoryImpl
import com.endumedia.herokick.ui.MainActivity
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector


/**
 * Created by Nino on 11.09.19
 */

@Suppress("unused")
@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeMainActivity(): MainActivity

    @Binds
    abstract fun bindProductsRepository(productsRepository: ProductsRepositoryImpl): ProductsRepository
}