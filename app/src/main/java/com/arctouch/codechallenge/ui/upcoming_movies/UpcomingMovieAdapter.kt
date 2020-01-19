package com.arctouch.codechallenge.ui.upcoming_movies

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.data.Cache
import com.arctouch.codechallenge.data.api.POSTER_URL
import com.arctouch.codechallenge.data.repository.NetworkState
import com.arctouch.codechallenge.model.Movie
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.movie_item.view.*
import kotlinx.android.synthetic.main.network_state_item.view.*
import java.text.SimpleDateFormat

class UpcomingMovieAdapter(
    private val onItemClickListener: (movie: Movie?) -> Unit
) :
    PagedListAdapter<Movie, RecyclerView.ViewHolder>(MovieDiffCallback()) {

    val MOVIE_VIEW_TYPE = 1
    val NETWORK_VIEW_TYPE = 2

    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view: View
        if (viewType == MOVIE_VIEW_TYPE) {
            view = layoutInflater.inflate(R.layout.movie_item, parent, false)
            return MovieItemViewHolder(
                view,
                onItemClickListener
            )
        } else {
            view = layoutInflater.inflate(R.layout.network_state_item, parent, false)
            return NetWorkStateItemViewHolder(
                view
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == MOVIE_VIEW_TYPE) {
            (holder as MovieItemViewHolder).bind(getItem(position))
        } else {
            (holder as NetWorkStateItemViewHolder).bind(networkState)
        }
    }

    private fun hasExtraRow(): Boolean {
        return networkState != null && networkState != NetworkState.LOADED
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            NETWORK_VIEW_TYPE
        } else {
            MOVIE_VIEW_TYPE
        }
    }

    //This information is used to detect if the contents of an item have changed.
    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }

    class MovieItemViewHolder(
        itemView: View,
        private val onItemClickListener: (movie: Movie?) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SimpleDateFormat")
        fun bind(movie: Movie?) {
            itemView.titleTextView.text = movie?.title
            val moviesGenres =
                (Cache.genres.filter { movie?.genreIds?.contains(it.id) == true }).joinToString(
                    separator = ", "
                ) { it.name }

            itemView.genresTextView.text = moviesGenres

            val parser = SimpleDateFormat("yyyy-MM-dd")
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            val dataFormatada = formatter.format(parser.parse(movie?.releaseDate))

            itemView.releaseDateTextView.text = dataFormatada

            val moviePosterURL = POSTER_URL + movie?.posterPath
            Glide.with(itemView.context)
                .load(moviePosterURL)
                .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                .into(itemView.posterImageView)

            itemView.setOnClickListener {
                onItemClickListener.invoke(movie)
            }
        }
    }

    class NetWorkStateItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(networkState: NetworkState?) {
            if (networkState != null && networkState == NetworkState.LOADING) {
                itemView.progress_bar_item.visibility = View.VISIBLE
            } else {
                itemView.progress_bar_item.visibility = View.GONE
            }

            if (networkState != null && networkState == NetworkState.ERROR) {
                itemView.error_msg_item.visibility = View.VISIBLE
                itemView.error_msg_item.text = networkState.msg
            } else if (networkState != null && networkState == NetworkState.END_OF_LIST) {
                itemView.error_msg_item.visibility = View.VISIBLE
                itemView.error_msg_item.text = networkState.msg
            } else {
                itemView.error_msg_item.visibility = View.GONE
            }
        }
    }

    fun setNetworkState(newNetworkState: NetworkState) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()

        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()

        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {

                //remove progressbar
                notifyItemRemoved(super.getItemCount())
            } else {
                //add progressbar
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != networkState) {
            notifyItemChanged(itemCount - 1)
        }
    }
}


