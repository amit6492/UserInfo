package com.example.userinfo.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "userInfo")
data class User(
    @SerializedName("first_name")
    val firstName: String,
    val gender: String,
    @PrimaryKey(autoGenerate = false)
    val id: String,
    @SerializedName("last_name")
    val lastName: String
)