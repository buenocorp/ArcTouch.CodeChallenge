package com.arctouch.codechallenge.ui.upcoming_movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.arctouch.codechallenge.data.repository.NetworkState
import com.arctouch.codechallenge.model.Movie
import io.reactivex.disposables.CompositeDisposable

class UpcomingMoviesViewModel(
    private val movieRepository: UpcomingMoviePageListRepository
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val moviePageList: LiveData<PagedList<Movie>> by lazy {
        movieRepository.fetchLiveMoviePageList(compositeDisposable)
    }

    val networkState: LiveData<NetworkState> by lazy {
        movieRepository.getNetWorkState()
    }

    fun listIsEmpty(): Boolean {
        return moviePageList.value?.isEmpty() ?: true
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}