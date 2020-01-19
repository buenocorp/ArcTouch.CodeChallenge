package com.arctouch.codechallenge.ui.movie_details

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.base.BaseActivity
import com.arctouch.codechallenge.data.api.ApiService
import com.arctouch.codechallenge.data.api.BACKDROP_URL
import com.arctouch.codechallenge.data.api.POSTER_URL
import com.arctouch.codechallenge.data.api.TmdbApi
import com.arctouch.codechallenge.data.repository.NetworkState
import com.arctouch.codechallenge.model.MovieDetails
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_movie_details.*
import kotlinx.android.synthetic.main.include_toolbar.*
import java.text.SimpleDateFormat


class MovieDetailsActivity : BaseActivity() {

    private lateinit var viewModel: MovieDetailsViewModel
    private lateinit var movieDetailsRepository: MovieDetailsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        setupToolbar(toolbar, R.string.details, true)

        val movieId = intent.getIntExtra(EXTRA_MOVIE_ID, 0)

        val apiService: TmdbApi = ApiService.getService()
        movieDetailsRepository = MovieDetailsRepository(apiService)

        viewModel = getViewModel(movieId)

        viewModel.movieDetails.observe(this, Observer {
            it?.let {
                bindUI(it)
            }
        })

        viewModel.networtState.observe(this, Observer {
            progress_bar.visibility = when (it == NetworkState.LOADING) {
                true -> View.VISIBLE
                false -> View.GONE
            }
            text_error.visibility = when (it == NetworkState.ERROR) {
                true -> View.VISIBLE
                false -> View.GONE
            }
        })
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun bindUI(movieDetails: MovieDetails) {
        movie.text = movieDetails.title
        subtitle.text = movieDetails.tagline
        rating.text = movieDetails.rating.toString()
        runtime.text = movieDetails.runtime.toString() + " minutos"
        overview.text = movieDetails.overview

        val parser = SimpleDateFormat("yyyy-MM-dd")
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val dataFormatada = formatter.format(parser.parse(movieDetails.releaseDate))

        release_date.text = dataFormatada

        genre.text = movieDetails.genres.joinToString(separator = ", ") { it.name }

        val moviePosterURL = POSTER_URL + movieDetails.posterPath
        Glide.with(this)
            .load(moviePosterURL)
            .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
            .into(poster)

        val movieBackDropURL = BACKDROP_URL + movieDetails.posterPath
        Glide.with(this)
            .load(movieBackDropURL)
            .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
            .into(backdrop)
    }

    private fun getViewModel(movieId: Int): MovieDetailsViewModel {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MovieDetailsViewModel(movieDetailsRepository, movieId) as T
            }
        })[MovieDetailsViewModel::class.java]
    }

    companion object {
        private const val EXTRA_MOVIE_ID = "EXTRA_MOVIE_ID"

        //return the call intent
        //That way, when another activity needs to call this, the same activity will know which parameters must be passed
        fun getStartIntent(
            context: Context,
            movieId: Int?
        ): Intent {
            return Intent(context, MovieDetailsActivity::class.java).apply {
                putExtra(EXTRA_MOVIE_ID, movieId)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        backActivity()
    }

    private fun backActivity() {
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                 backActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
