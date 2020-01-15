package com.hwx.kt_moxy_coroutines_test.view

import android.view.View
import com.hwx.kt_moxy_coroutines_test.model.FilmCredit
import com.hwx.kt_moxy_coroutines_test.model.FilmSimple
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface MainView : MvpView {

    fun onUpdateFilmsCarouselData(filmsList: Array<FilmSimple>)

    fun goToFilmDescription(
        filmSimple: FilmSimple,
        filmCredit: FilmCredit,
        viewWrapper: View,
        viewImage: View,
        viewCaption: View,
        viewGenres: View,
        viewRatingText: View,
        viewRatingBar: View
    )
}