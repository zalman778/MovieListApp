package com.hwx.kt_moxy_coroutines_test.di

import android.content.Context
import com.hwx.kt_moxy_coroutines_test.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
          AndroidSupportInjectionModule::class
        , NetworkModules::class
        , ContextModule::class
        , ActivityModule::class
    ]
)
interface ApplicationComponent: AndroidInjector<Application> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: android.app.Application): Builder

        fun build(): ApplicationComponent
    }

    fun context(): Context
}