package com.hwx.kt_moxy_coroutines_test.di

import com.hwx.kt_moxy_coroutines_test.activity.FilmDescriptionActivity
import com.hwx.kt_moxy_coroutines_test.activity.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun bindFilmDescriptionActivity(): FilmDescriptionActivity
}