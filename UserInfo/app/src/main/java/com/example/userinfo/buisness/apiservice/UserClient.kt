package com.example.userinfo.buisness.apiservice

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


const val API_KEY = "YsWTCNH3FO8oq8bbnB3Pc0_OCw1cVFsqEyGG"
const val FIRST_PAGE = 1
const val Format = "json"
const val PAGE_SIZE = 20

const val BASE_URL = "https://gorest.co.in/"

object UserClient {

    fun getClient(): Api{

        val requestInterceptor =  Interceptor {
            chain ->
            val url = chain.request()
                .url()
                .newBuilder()
                .addQueryParameter("_format", Format)
                .addQueryParameter("access-token", API_KEY)
                .build()

            val request = chain.request()
                .newBuilder()
                .url(url)
                .build()

            return@Interceptor chain.proceed(request)
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(requestInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)
    }
}