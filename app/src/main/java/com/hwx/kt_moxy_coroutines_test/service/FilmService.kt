package com.hwx.kt_moxy_coroutines_test.service

import com.hwx.kt_moxy_coroutines_test.model.FilmCredit
import com.hwx.kt_moxy_coroutines_test.model.FilmDetail
import com.hwx.kt_moxy_coroutines_test.model.FilmsArray
import com.hwx.kt_moxy_coroutines_test.model.GenresArray
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Url

interface FilmService  {

    @GET
    fun fetchFilmsListAsync(@Url url: String): Deferred<FilmsArray>

    @GET
    fun fetchFilmsCastAsync(@Url url: String): Deferred<FilmCredit>

    @GET
    fun fetchFilmDetailAsync(@Url url: String): Deferred<FilmDetail>

    @GET
    fun fetchFilmGenresAsync(@Url url: String): Deferred<GenresArray>

}