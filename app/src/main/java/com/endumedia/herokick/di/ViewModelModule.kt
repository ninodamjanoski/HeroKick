package com.endumedia.herokick.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.endumedia.herokick.ui.details.ItemDetailsViewModel
import com.endumedia.herokick.ui.productslist.ProductsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


/**
 * Created by Nino on 19.08.19
 */
@Suppress("unused")
@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ProductsViewModel::class)
    abstract fun bindFetchCodesViewModel(fetchCodesViewModel: ProductsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ItemDetailsViewModel::class)
    abstract fun bindItemDetailsViewModel(itemDetailsViewModel: ItemDetailsViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: HeroKickViewModelFactory): ViewModelProvider.Factory
}
