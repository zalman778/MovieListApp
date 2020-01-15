package com.hwx.kt_moxy_coroutines_test.presenter

import android.view.View
import com.hwx.Configuration
import com.hwx.kt_moxy_coroutines_test.ValuesHolder
import com.hwx.kt_moxy_coroutines_test.exec.FilmCreditJob
import com.hwx.kt_moxy_coroutines_test.exec.FilmListJob
import com.hwx.kt_moxy_coroutines_test.model.FilmSimple
import com.hwx.kt_moxy_coroutines_test.view.MainView
import moxy.InjectViewState
import moxy.MvpPresenter
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class MainPresenter
    @Inject constructor(
          private val filmListJob: FilmListJob
        , private val filmCreditJob: FilmCreditJob
    ) : MvpPresenter<MainView>() {

    fun loadData() {
        Timber.w("on load data...")
        filmListJob.execute{
            onGetUrl { Configuration.getBaseUrlList(0) }
            onComplete {
                val filmsArray = it.first
                val genresArray = it.second
                genresArray.genres.forEach { ValuesHolder.cachedGenres[it.id] = it.name }
                viewState.onUpdateFilmsCarouselData(filmsArray.results.take(8).toTypedArray())
            }
            onException {
                Timber.tag("AVX").e(it.message)
            }

        }
    }

    fun onFilmClick(filmSimple: FilmSimple, viewWrapper: View, viewImage: View, viewCaption: View
                    , viewGenres: View, viewRatingText: View, viewRatingBar: View) {
        filmCreditJob.execute{
            onGetUrl { Configuration.getMovieCredit(filmSimple.id) }
            onComplete {
                viewState.goToFilmDescription(filmSimple, it, viewWrapper, viewImage, viewCaption, viewGenres, viewRatingText, viewRatingBar)
            }
            onException {
                Timber.tag("AVX").e(it.message)
            }
        }
    }
}