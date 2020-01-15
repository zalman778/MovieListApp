package com.hwx.kt_moxy_coroutines_test.exec

import com.hwx.Configuration
import com.hwx.kt_moxy_coroutines_test.model.FilmsArray
import com.hwx.kt_moxy_coroutines_test.model.GenresArray
import com.hwx.kt_moxy_coroutines_test.service.FilmRepository

/**
 * sending two parallel requests...
 */
class FilmListJob(
    private val filmRepository: FilmRepository
): BaseJob<Pair<FilmsArray, GenresArray>>() {

    override suspend fun executeInBackground(url: String): Pair<FilmsArray, GenresArray> {
        val filmsArray = filmRepository.getFilmsListAsync(url)
        val genresArray = filmRepository.getFilmsGenresAsync(Configuration.getGenresListUrl())

        return Pair(filmsArray.await(), genresArray.await())
    }

}