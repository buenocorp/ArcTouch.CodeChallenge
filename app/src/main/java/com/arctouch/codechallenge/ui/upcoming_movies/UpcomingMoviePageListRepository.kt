package com.arctouch.codechallenge.ui.upcoming_movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.arctouch.codechallenge.data.api.POST_PER_PAGE
import com.arctouch.codechallenge.data.api.TmdbApi
import com.arctouch.codechallenge.data.repository.MovieDataSource
import com.arctouch.codechallenge.data.repository.MovieDataSourceFactory
import com.arctouch.codechallenge.data.repository.NetworkState
import com.arctouch.codechallenge.model.Movie
import io.reactivex.disposables.CompositeDisposable

class UpcomingMoviePageListRepository(
    private val apiService: TmdbApi
) {
    lateinit var moviePageList: LiveData<PagedList<Movie>>
    lateinit var movieDataSourceFactory: MovieDataSourceFactory

    fun fetchLiveMoviePageList(
        compositeDisposable: CompositeDisposable
    ): LiveData<PagedList<Movie>> {
        movieDataSourceFactory = MovieDataSourceFactory(apiService, compositeDisposable)

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(POST_PER_PAGE)
            .build()


        moviePageList = LivePagedListBuilder(movieDataSourceFactory, config).build()

        return moviePageList
    }

    //The function passed to switchMap () needs to return a LiveData object
    //Transformation methods are used to transport information throughout the observer's life cycle.
    fun getNetWorkState(): LiveData<NetworkState> {
        return Transformations.switchMap<MovieDataSource, NetworkState>(
            movieDataSourceFactory.movieLiveDataSource, MovieDataSource::networkState
        )
    }
}