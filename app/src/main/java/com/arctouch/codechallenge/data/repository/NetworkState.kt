package com.arctouch.codechallenge.data.repository

enum class Status {
    LOADING,
    LOADED,
    ERROR,
    END_OF_LIST
}

class NetworkState(val status: Status, val msg: String) {
    companion object {

        val LOADING: NetworkState
        val LOADED: NetworkState
        val ERROR: NetworkState
        val END_OF_LIST:NetworkState

        init {
            LOADING = NetworkState(Status.LOADING, "Loading")
            LOADED = NetworkState(Status.LOADED, "Loaded")
            ERROR = NetworkState(Status.ERROR, "Network problems")
            END_OF_LIST = NetworkState(Status.END_OF_LIST, "End of list of films")
        }
    }
}