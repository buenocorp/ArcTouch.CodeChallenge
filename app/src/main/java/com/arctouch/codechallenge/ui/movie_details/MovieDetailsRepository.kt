package com.arctouch.codechallenge.ui.movie_details

import androidx.lifecycle.LiveData
import com.arctouch.codechallenge.data.api.TmdbApi
import com.arctouch.codechallenge.data.repository.MovieDetailsNetworkDataSource
import com.arctouch.codechallenge.data.repository.NetworkState
import com.arctouch.codechallenge.model.MovieDetails
import io.reactivex.disposables.CompositeDisposable

class MovieDetailsRepository(private val apiService: TmdbApi) {

    lateinit var movieDetailsNetworkDataSource: MovieDetailsNetworkDataSource

    fun fetchMoviesDetails(
        compositeDisposable: CompositeDisposable,
        movieId: Int
    ): LiveData<MovieDetails> {
        movieDetailsNetworkDataSource =
            MovieDetailsNetworkDataSource(apiService, compositeDisposable)
        movieDetailsNetworkDataSource.fetchMovieDetails(movieId)

        return movieDetailsNetworkDataSource.downloadedMovieResponse
    }

    fun getMovieDetailsNetworkState(): LiveData<NetworkState> {
        return movieDetailsNetworkDataSource.networkState
    }
}