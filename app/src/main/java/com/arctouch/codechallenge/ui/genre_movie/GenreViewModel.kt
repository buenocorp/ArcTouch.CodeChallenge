package com.arctouch.codechallenge.ui.genre_movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.arctouch.codechallenge.data.repository.NetworkState
import com.arctouch.codechallenge.model.Genre
import com.arctouch.codechallenge.ui.genre_movie.GenreRepository
import io.reactivex.disposables.CompositeDisposable

class GenreViewModel(
    private val genreRepository: GenreRepository
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val genreList: LiveData<List<Genre>> by lazy {
        genreRepository.fetchGenreList(compositeDisposable)
    }

    val networkState: LiveData<NetworkState> by lazy {
        genreRepository.getGenreNetworkState()
    }

    fun listIsEmpty(): Boolean {
        return genreList.value?.isEmpty() ?: true
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}