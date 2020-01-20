package com.arctouch.codechallenge.data.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.arctouch.codechallenge.data.api.TmdbApi
import com.arctouch.codechallenge.model.Movie
import io.reactivex.disposables.CompositeDisposable

class MovieDataSourceFactory(
    private val apiService: TmdbApi,
    private val compositeDisposable: CompositeDisposable

) : DataSource.Factory<Int, Movie>() {
    val movieLiveDataSource = MutableLiveData<MovieDataSource>()

    override fun create(): DataSource<Int, Movie> {
        val movieDataSource = MovieDataSource(apiService, compositeDisposable)
        movieLiveDataSource.postValue(movieDataSource)

        return movieDataSource
    }
}