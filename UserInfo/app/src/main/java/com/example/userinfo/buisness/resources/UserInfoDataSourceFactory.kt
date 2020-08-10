package com.example.userinfo.buisness.resources

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.userinfo.buisness.apiservice.Api
import com.example.userinfo.buisness.db.UserInfoDatabase
import com.example.userinfo.model.User

class UserInfoDataSourceFactory(private val context: Context, private val api: Api,
                                private val userInfoDatabase: UserInfoDatabase)
    : DataSource.Factory<Int, User>() {

    val userLiveDataSource = MutableLiveData<UserInfoDataSource>()
    override fun create(): DataSource<Int, User> {
        val userInfoDataSource = UserInfoDataSource(context, api, userInfoDatabase)
        userLiveDataSource.postValue(userInfoDataSource)
        return userInfoDataSource
    }

}