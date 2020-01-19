package com.arctouch.codechallenge.ui.genre_movie

import androidx.lifecycle.LiveData
import com.arctouch.codechallenge.data.api.TmdbApi
import com.arctouch.codechallenge.data.repository.GenreDataSource
import com.arctouch.codechallenge.data.repository.NetworkState
import com.arctouch.codechallenge.model.Genre
import io.reactivex.disposables.CompositeDisposable

class GenreRepository(private val apiService: TmdbApi) {

    lateinit var genreDataSource: GenreDataSource

    fun fetchGenreList(
        compositeDisposable: CompositeDisposable
    ): LiveData<List<Genre>> {
        genreDataSource =
            GenreDataSource(apiService, compositeDisposable)
        genreDataSource.fetchGenreList()

        return genreDataSource.downloadedGenres
    }

    fun getGenreNetworkState(): LiveData<NetworkState> {
        return genreDataSource.networkState
    }
}