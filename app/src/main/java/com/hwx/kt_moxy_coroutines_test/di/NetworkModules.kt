package com.hwx.kt_moxy_coroutines_test.di

import android.text.format.DateUtils
import com.google.gson.Gson
import com.hwx.kt_moxy_coroutines_test.BuildConfig
import com.hwx.kt_moxy_coroutines_test.exec.FilmCreditJob
import com.hwx.kt_moxy_coroutines_test.exec.FilmListJob
import com.hwx.kt_moxy_coroutines_test.service.FilmRepository
import com.hwx.kt_moxy_coroutines_test.service.FilmRepositoryImpl
import com.hwx.kt_moxy_coroutines_test.service.FilmService
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModules {

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideGsonConvertorFactory(): GsonConverterFactory = GsonConverterFactory.create()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val client = OkHttpClient.Builder()
            .connectTimeout(DateUtils.MINUTE_IN_MILLIS, TimeUnit.MILLISECONDS)
            .writeTimeout(DateUtils.MINUTE_IN_MILLIS, TimeUnit.MILLISECONDS)
            .readTimeout(DateUtils.MINUTE_IN_MILLIS, TimeUnit.MILLISECONDS)
        val interceptor = HttpLoggingInterceptor()
        interceptor.level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        client.addInterceptor(interceptor)
        return client.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        factory: GsonConverterFactory,
        client: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BaseUrl)
        .addConverterFactory(factory)
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(client)
        .build()

    @Provides
    @Singleton
    fun provideFilmService(retrofit: Retrofit): FilmService = retrofit.create(FilmService::class.java)

    @Provides
    @Singleton
    fun provideApiRepository(filmService: FilmService): FilmRepository = FilmRepositoryImpl(filmService)

    @Provides
    fun provideFilmListJob(filmRepository: FilmRepository): FilmListJob = FilmListJob(filmRepository)

    @Provides
    fun provideFilmCastJob(filmRepository: FilmRepository): FilmCreditJob = FilmCreditJob(filmRepository)


}