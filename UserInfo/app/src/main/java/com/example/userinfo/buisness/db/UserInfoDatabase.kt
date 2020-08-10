package com.example.userinfo.buisness.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.userinfo.model.User


@Database(entities = [User::class], version = 1)
abstract class UserInfoDatabase : RoomDatabase() {

    companion object{
        var INSTANCE : UserInfoDatabase? = null
        const val DATABASE_NAME = "UserInfoDatabase"

        fun getInstance(context: Context): UserInfoDatabase?{
            if(INSTANCE == null)
                synchronized(UserInfoDatabase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                    UserInfoDatabase::class.java, DATABASE_NAME)
                        .build()
                }
            return INSTANCE

        }
    }

    abstract fun userDao(): UserDao
}