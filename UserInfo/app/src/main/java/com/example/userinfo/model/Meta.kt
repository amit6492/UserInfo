package com.example.userinfo.model


data class Meta(
    val code: Int,
    val currentPage: Int,
    val message: String,
    val pageCount: Int,
    val perPage: Int,
    val success: Boolean,
    val totalCount: Int
)