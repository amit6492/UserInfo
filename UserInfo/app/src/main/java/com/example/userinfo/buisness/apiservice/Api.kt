package com.example.userinfo.buisness.apiservice

import com.example.userinfo.model.UserResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("public-api/users?")
    fun getUserInfo(@Query("page") page: Int): Call<UserResponse>
}