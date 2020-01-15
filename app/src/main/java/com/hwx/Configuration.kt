package com.hwx

import com.hwx.kt_moxy_coroutines_test.BuildConfig

object Configuration {
    var API_KEY = BuildConfig.ApiKey
    var BASE_API_URL = "https://api.themoviedb.org/3/"
    var LOCALE_CODE = "en-US"//"ru-RU"



    fun getBaseUrlList(pPage: Int?): String {
        var page = pPage
        if (page == null)
            page = 1
        return BASE_API_URL + "discover/movie?api_key=$API_KEY&language=$LOCALE_CODE&sort_by=popularity.desc&include_adult=true&include_video=false&page=$page/"
    }

    fun getMovieCredit(pId: Long): String {
        return BASE_API_URL + "movie/$pId/credits?api_key=$API_KEY&language=$LOCALE_CODE"
    }

    fun getImageFullUrl(imageHash: String): String {
        return "https://image.tmdb.org/t/p/w600_and_h900_bestv2$imageHash"
    }

    fun getGenresListUrl(): String {
        return BASE_API_URL + "genre/movie/list?api_key=$API_KEY&language=$LOCALE_CODE"
    }
}