package com.hwx.kt_moxy_coroutines_test.service

import com.hwx.kt_moxy_coroutines_test.model.*
import kotlinx.coroutines.Deferred

class FilmRepositoryImpl(private val filmService: FilmService): FilmRepository {

    override suspend fun getFilmsListAsync(url: String): Deferred<FilmsArray> = filmService.fetchFilmsListAsync(url)

    override suspend fun getFilmCreditAsync(url: String): Deferred<FilmCredit> = filmService.fetchFilmsCastAsync(url)

    override suspend fun getFilmDetailAsync(url: String): Deferred<FilmDetail> = filmService.fetchFilmDetailAsync(url)

    override suspend fun getFilmsGenresAsync(url: String): Deferred<GenresArray> = filmService.fetchFilmGenresAsync(url)
}