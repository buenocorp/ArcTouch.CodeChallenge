package com.arctouch.codechallenge.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val BASE_URL = "https://api.themoviedb.org/3/"
const val API_KEY = "4f7610083e6184fffba296e844fa2a50"

const val POSTER_URL = "https://image.tmdb.org/t/p/w154"
const val BACKDROP_URL = "https://image.tmdb.org/t/p/w780"

const val DEFAULT_LANGUAGE = "pt-BR"
const val DEFAULT_REGION = "BR"

const val FIRST_PAGE = 1
const val POST_PER_PAGE = 20

object ApiService {

    fun getService(): TmdbApi {
        //To customize our requests, such as adding a header or a log, for example, we need to override OkHttpClient and add an Interceptor
        val interceptor = Interceptor { chain ->
            val url = chain.request()
                .url()
                .newBuilder()
                .addQueryParameter("api_key", API_KEY)
                .addQueryParameter("language", DEFAULT_LANGUAGE)
                //.addQueryParameter("region", DEFAULT_REGION)
                .build()

            val request = chain.request()
                .newBuilder()
                .url(url)
                .build()

            return@Interceptor chain.proceed(request)
        }

        //It is a HTTP client responsible for any low-level network operation, caching, request and response manipulation
        val okHtttp = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()

        //URL manipulation, requesting, loading, caching, threading, synchronization... It allows sync and async calls.
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHtttp)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbApi::class.java)
    }
}