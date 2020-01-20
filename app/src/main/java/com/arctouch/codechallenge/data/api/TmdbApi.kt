package com.arctouch.codechallenge.data.api

import com.arctouch.codechallenge.model.GenreResponse
import com.arctouch.codechallenge.model.MovieDetails
import com.arctouch.codechallenge.model.UpcomingMoviesResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    @GET("genre/movie/list")
    fun getGenres(
    ): Single<GenreResponse>

    @GET("movie/upcoming")
    fun getUpcomingMovies(
        @Query("page") page: Int
    ): Single<UpcomingMoviesResponse>

    @GET("movie/{movie_id}")
    fun getMovieDetails(
        @Path("movie_id") id: Int
    ): Single<MovieDetails>
}
