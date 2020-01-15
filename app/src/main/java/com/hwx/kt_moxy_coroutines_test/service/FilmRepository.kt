package com.hwx.kt_moxy_coroutines_test.service

import com.hwx.kt_moxy_coroutines_test.model.*
import kotlinx.coroutines.Deferred

interface FilmRepository  {

    suspend fun getFilmsListAsync(url: String): Deferred<FilmsArray>

    suspend fun getFilmCreditAsync(url: String): Deferred<FilmCredit>

    suspend fun getFilmDetailAsync(url: String): Deferred<FilmDetail>

    suspend fun getFilmsGenresAsync(url: String): Deferred<GenresArray>
}