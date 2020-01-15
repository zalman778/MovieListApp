package com.hwx.kt_moxy_coroutines_test.exec


import com.hwx.kt_moxy_coroutines_test.model.FilmCredit
import com.hwx.kt_moxy_coroutines_test.service.FilmRepository

class FilmCreditJob(
    private val filmRepository: FilmRepository
): BaseJob<FilmCredit>() {
    override suspend fun executeInBackground(url: String): FilmCredit {
        return filmRepository.getFilmCreditAsync(url).await()
    }

}