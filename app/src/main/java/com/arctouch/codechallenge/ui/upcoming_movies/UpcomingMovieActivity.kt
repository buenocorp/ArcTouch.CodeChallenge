package com.arctouch.codechallenge.ui.upcoming_movies

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.base.BaseActivity
import com.arctouch.codechallenge.data.Cache
import com.arctouch.codechallenge.data.api.ApiService
import com.arctouch.codechallenge.data.api.TmdbApi
import com.arctouch.codechallenge.data.repository.NetworkState
import com.arctouch.codechallenge.ui.genre_movie.GenreRepository
import com.arctouch.codechallenge.ui.genre_movie.GenreViewModel
import com.arctouch.codechallenge.ui.movie_details.MovieDetailsActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.include_toolbar.*

class UpcomingMovieActivity : BaseActivity() {

    private lateinit var viewModel: UpcomingMoviesViewModel

    private lateinit var viewModelGenre: GenreViewModel

    lateinit var movieRepository: UpcomingMoviePageListRepository
    lateinit var genreRepository: GenreRepository

    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupToolbar(toolbar, R.string.upcoming)

        setViewlModel()
        bindGenres()
        bindMovies()
    }

    private fun setViewlModel() {
        val apiService: TmdbApi = ApiService.getService()
        movieRepository =
            UpcomingMoviePageListRepository(
                apiService
            )
        genreRepository =
            GenreRepository(
                apiService
            )

        viewModelGenre = getViewModelGenre()
        viewModel = getViewModel()
    }

    private fun bindGenres() {
        viewModelGenre.genreList.observe(this, Observer {
            Cache.genres = it
        })

        viewModelGenre.networkState.observe(this, Observer {
            recyclerView.visibility =
                when (viewModelGenre.listIsEmpty() && (it == NetworkState.LOADING || it == NetworkState.ERROR)) {
                    true -> View.GONE
                    false -> View.VISIBLE
                }

            progressBar.visibility =
                when (viewModelGenre.listIsEmpty() && it == NetworkState.LOADING) {
                    true -> View.VISIBLE
                    false -> View.GONE
                }
            text_error.visibility =
                when (viewModelGenre.listIsEmpty() && it == NetworkState.ERROR) {
                    true -> View.VISIBLE
                    false -> View.GONE
                }
        })
    }

    private fun bindMovies() {
        val movieAdapter =
            UpcomingMovieAdapter { movie ->

                val intent = MovieDetailsActivity.getStartIntent(
                    this, movie?.id
                )
                this.startActivity(intent)
            }

        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = movieAdapter

        viewModel.moviePageList.observe(this, Observer {
            movieAdapter.submitList(it)
        })

        viewModel.networkState.observe(this, Observer {
            progressBar.visibility = when (viewModel.listIsEmpty() && it == NetworkState.LOADING) {
                true -> View.VISIBLE
                false -> View.GONE
            }
            text_error.visibility = when (viewModel.listIsEmpty() && it == NetworkState.ERROR) {
                true -> View.VISIBLE
                false -> View.GONE
            }

            if (!viewModel.listIsEmpty()) {
                movieAdapter.setNetworkState(it)
            }
        }
        )
    }

    private fun getViewModel(): UpcomingMoviesViewModel {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return UpcomingMoviesViewModel(
                    movieRepository
                ) as T
            }
        })[UpcomingMoviesViewModel::class.java]
    }

    private fun getViewModelGenre(): GenreViewModel {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return GenreViewModel(
                    genreRepository
                ) as T
            }
        })[GenreViewModel::class.java]
    }

    override fun onBackPressed() {
        val t = System.currentTimeMillis()
        if (t - backPressedTime > 2000) { // 2 secs
            backPressedTime = t
            showSnack("Pressione novamente para sair")
        } else {
            super.onBackPressed()
        }
    }

    private fun showSnack(msg: String) {
        Snackbar.make(
            linearLayout,
            msg, Snackbar.LENGTH_SHORT
        )
            .setTextColor(Color.WHITE)
            .setBackgroundTint(ContextCompat.getColor(this, R.color.colorPrimary))
            .show()
    }
}
