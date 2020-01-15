package com.hwx.kt_moxy_coroutines_test.model

data class GenresArray(
    val genres: Array<Genre>
)

data class Genre(
    val id: Int,
    val name: String
)
