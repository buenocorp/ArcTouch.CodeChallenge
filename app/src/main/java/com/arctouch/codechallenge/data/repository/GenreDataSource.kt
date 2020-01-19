package com.arctouch.codechallenge.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arctouch.codechallenge.data.api.TmdbApi
import com.arctouch.codechallenge.model.Genre
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class GenreDataSource(
    private val tmdbApi: TmdbApi,
    private val compositeDisposable: CompositeDisposable
) {

    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        //using this get, no need to implement get function to get networkState
        get() = _networkState

    private val _downloadedGenres = MutableLiveData<List<Genre>>()
    val downloadedGenres: LiveData<List<Genre>>
        get() = _downloadedGenres

    fun fetchGenreList() {
        _networkState.postValue(NetworkState.LOADING)

        try {
            compositeDisposable.add(
                tmdbApi.getGenres()
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        _downloadedGenres.postValue(it.genres)
                        _networkState.postValue(NetworkState.LOADED)
                    },
                        {
                            _networkState.postValue(NetworkState.ERROR)
                            Log.e("GenreDataSource", it.message)

                        })
            )
        } catch (e: Exception) {
            Log.e("GenreDataSource", e.message)
        }
    }
}