package com.example.userinfo.model


import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("_meta")
    val meta: Meta,
    @SerializedName("result")
    val user: List<User>
)