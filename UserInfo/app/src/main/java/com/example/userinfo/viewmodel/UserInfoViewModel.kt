package com.example.userinfo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.example.userinfo.buisness.resources.NetworkState
import com.example.userinfo.model.User
import com.example.userinfo.repo.UserInfoRepo

class UserInfoViewModel(private val userInfoRepo: UserInfoRepo, application: Application)
    : AndroidViewModel(application){

    val userInfoPageList: LiveData<PagedList<User>> by lazy {
        userInfoRepo.fetchLiveUserPagedList()
    }

    val networkState: LiveData<NetworkState>  by lazy {
        userInfoRepo.getNetworkState()
    }

    fun isListEmpty(): Boolean{
        return userInfoPageList.value?.isEmpty() ?:true
    }
}