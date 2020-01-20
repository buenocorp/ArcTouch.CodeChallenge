package com.arctouch.codechallenge.ui.movie_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.arctouch.codechallenge.data.repository.NetworkState
import com.arctouch.codechallenge.model.MovieDetails
import io.reactivex.disposables.CompositeDisposable

class MovieDetailsViewModel(
    private val movieDetailsRepository: MovieDetailsRepository,
    movieId: Int
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val movieDetails: LiveData<MovieDetails> by lazy {
        movieDetailsRepository.fetchMoviesDetails(compositeDisposable, movieId)
    }

    val networtState: LiveData<NetworkState> by lazy {
        movieDetailsRepository.getMovieDetailsNetworkState()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}