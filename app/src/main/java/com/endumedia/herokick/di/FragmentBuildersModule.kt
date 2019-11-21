package com.endumedia.herokick.di

import com.endumedia.herokick.ui.details.DetailsFragment
import com.endumedia.herokick.ui.productslist.ProductsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


/**
 * Created by Nino on 11.09.19
 */
@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeProductsFragment(): ProductsFragment

    @ContributesAndroidInjector
    abstract fun contributeDetailsFragment(): DetailsFragment
}