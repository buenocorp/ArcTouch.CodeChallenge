package com.arctouch.codechallenge.model

import com.google.gson.annotations.SerializedName

data class Movie(
    val id: Int,
    val title: String,
    val overview: String?,
    @SerializedName("genre_ids")
    val genreIds: List<Int>?,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName(
        "backdrop_path"
    ) val backdropPath: String?,
    @SerializedName("release_date")
    val releaseDate: String?,
    val genres: List<Genre>?
)