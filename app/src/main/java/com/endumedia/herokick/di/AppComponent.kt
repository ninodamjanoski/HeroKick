package com.endumedia.herokick.di

import android.app.Application
import com.endumedia.herokick.HeroKickApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton


/**
 * Created by Nino on 11.09.19
 */

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        MainActivityModule::class]
)
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(pottyApp: HeroKickApp)

}
