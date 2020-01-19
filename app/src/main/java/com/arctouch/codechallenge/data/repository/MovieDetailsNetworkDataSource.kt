package com.arctouch.codechallenge.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arctouch.codechallenge.data.api.TmdbApi
import com.arctouch.codechallenge.model.MovieDetails
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MovieDetailsNetworkDataSource(
    private val tmdbApi: TmdbApi,
    private val compositeDisposable: CompositeDisposable
) {

    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        //using this get, no need to implement get function to get networkState
        get() = _networkState

    private val _downloadedMovieDetails = MutableLiveData<MovieDetails>()
    val downloadedMovieResponse: LiveData<MovieDetails>
        get() = _downloadedMovieDetails

    fun fetchMovieDetails(movieId: Int) {
        _networkState.postValue(NetworkState.LOADING)

        try {
            compositeDisposable.add(
                tmdbApi.getMovieDetails(movieId)
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        _downloadedMovieDetails.postValue(it)
                        _networkState.postValue(NetworkState.LOADED)
                    },
                        {
                            _networkState.postValue(NetworkState.ERROR)
                            Log.e("MovieDetailsDataSource", it.message)

                        })
            )
        } catch (e: Exception) {
            Log.e("MovieDetailsDataSource", e.message)
        }
    }
}