package com.hwx.kt_moxy_coroutines_test.model

import com.google.gson.annotations.SerializedName


data class FilmsArray(
    @SerializedName("results") val results: List<FilmSimple>
)

data class FilmSimple(
    val id: Long,
    @SerializedName("vote_average") val voteAverage: Double,
    val title: String,
    var overview: String,
    val popularity: String,
    @SerializedName("poster_path") val posterPath: String,
    @SerializedName("backdrop_path") val backdropPath: String,

    @SerializedName("genre_ids") var genreArray: List<Int>
): java.io.Serializable

data class FilmDetail(
    @SerializedName("backdrop_path") var backdropPath: String,
    @SerializedName("poster_path") var posterPath: String,

    //in card:
    var title: String,
    @SerializedName("original_title") var originalTitle: String,
    var overview: String,
    var popularity: Double,
    var budget: Long,
    @SerializedName("release_date") var releaseDate: String,
    @SerializedName("vote_average") var voteAverage: Double,
    @SerializedName("vote_count") var voteCount: Int,
    var homepage: String

)
data class FilmCredit(
    @SerializedName("cast") val casts: List<FilmCast>,
    val crew: List<FilmCrewMember>
): java.io.Serializable
data class FilmCast(
    var name: String,
    @SerializedName("profile_path") var profilePath: String
): java.io.Serializable

data class FilmCrewMember(
    var name: String,
    var job: String,
    @SerializedName("profile_path") var profilePath: String
): java.io.Serializable