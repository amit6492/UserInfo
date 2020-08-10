package com.example.userinfo.repo

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.userinfo.buisness.apiservice.Api
import com.example.userinfo.buisness.apiservice.PAGE_SIZE
import com.example.userinfo.buisness.db.UserInfoDatabase
import com.example.userinfo.buisness.resources.NetworkState
import com.example.userinfo.buisness.resources.UserInfoDataSource
import com.example.userinfo.buisness.resources.UserInfoDataSourceFactory
import com.example.userinfo.model.User
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class UserInfoRepo(val context: Context, private val userInfoDatabase: UserInfoDatabase,
                   private val api: Api) {


    lateinit var userInfoPagedList: LiveData<PagedList<User>>
    lateinit var userInfoDataSourceFactory: UserInfoDataSourceFactory

    fun fetchLiveUserPagedList(): LiveData<PagedList<User>>{

        val executor: Executor = Executors.newFixedThreadPool(2)
        userInfoDataSourceFactory = UserInfoDataSourceFactory(context, api, userInfoDatabase)
        val config: PagedList.Config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(20)
            .setPageSize(PAGE_SIZE)
            .build()

        userInfoPagedList = LivePagedListBuilder(userInfoDataSourceFactory, config)
            .setFetchExecutor(executor)
            .build()

        return userInfoPagedList
    }

    fun getNetworkState(): LiveData<NetworkState>{
        return Transformations.switchMap<UserInfoDataSource, NetworkState>(
            userInfoDataSourceFactory.userLiveDataSource, UserInfoDataSource::networkState
        )
    }

}