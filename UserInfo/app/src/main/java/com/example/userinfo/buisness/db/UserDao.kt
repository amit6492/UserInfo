package com.example.userinfo.buisness.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.userinfo.model.User


@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: List<User>)

    @Query("SELECT * FROM userInfo")
    fun getAllUsers(): List<User>

    @Query("DELETE FROM userInfo")
    fun deleteAll()
}