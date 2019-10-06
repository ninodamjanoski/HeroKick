package com.endumedia.herokick

import android.app.Activity
import android.app.Application
import com.endumedia.herokick.di.AppInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject


/**
 * Created by Nino on 02.10.19
 */
class HeroKickApp : Application(), HasActivityInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)
    }

    override fun activityInjector() = dispatchingAndroidInjector
}