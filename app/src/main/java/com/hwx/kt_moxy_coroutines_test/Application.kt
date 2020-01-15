package com.hwx.kt_moxy_coroutines_test

import com.hwx.kt_moxy_coroutines_test.di.ApplicationComponent
import com.hwx.kt_moxy_coroutines_test.di.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber

class Application: DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        appComponent = DaggerApplicationComponent
            .builder()
            .application(this)
            .build()
        return appComponent
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            //TODO: Logic for crashlitics
        }
    }

    companion object {
        lateinit var appComponent: ApplicationComponent
    }

}