package com.example.userinfo.buisness.resources

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.example.userinfo.buisness.apiservice.Api
import com.example.userinfo.buisness.apiservice.FIRST_PAGE
import com.example.userinfo.buisness.db.UserInfoDatabase
import com.example.userinfo.model.User
import com.example.userinfo.model.UserResponse
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Callback
import retrofit2.Response

class UserInfoDataSource(private val context: Context, private val api: Api,
                         private val userInfoDatabase: UserInfoDatabase)
    : PageKeyedDataSource<Int, User>() {

    private val page = FIRST_PAGE
    val networkState: MutableLiveData<NetworkState> = MutableLiveData()

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, User>
    ) {
        networkState.postValue(NetworkState.LOADING)
        var isConnected = isOnline(context)
        Single.fromCallable(
            {
                if (isConnected) {
                    userInfoDatabase.userDao().deleteAll()
                } else {
                    getUserInfoFromDb()
                }
            }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                        users -> fetchCallback(users, isConnected, callback)
                },
                {
                    networkState.postValue(NetworkState.ERROR)
                }
            )

    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, User>) {
        networkState.postValue(NetworkState.LOADING)
        fetchData(params.key+1, callback, params)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, User>) {}



    fun fetchData(pageNumber: Int, callback: LoadCallback<Int, User>, params: LoadParams<Int>){
        api.getUserInfo(pageNumber).enqueue(object : Callback<UserResponse> {
            override fun onFailure(call: retrofit2.Call<UserResponse>, t: Throwable) {
                networkState.postValue(NetworkState.ERROR)
            }

            override fun onResponse(
                call: retrofit2.Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                Single.fromCallable{
                    getAllIndoFromDb(response, params.key)
                }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                                users -> callback.onResult(users, params.key+1)
                        },
                        {
                            networkState.postValue(NetworkState.ERROR)
                            Log.e("UserDataSource", it.message)
                        }
                    )

            }
        })
    }

    fun fetchInitialData(pageNumber: Int, callback: LoadInitialCallback<Int, User>){
        api.getUserInfo(pageNumber).enqueue(object : Callback<UserResponse> {
            override fun onFailure(call: retrofit2.Call<UserResponse>, t: Throwable) {
                networkState.postValue(NetworkState.ERROR)
            }

            override fun onResponse(
                call: retrofit2.Call<UserResponse>,
                response: Response<UserResponse>
            ) {

                Single.fromCallable{
                    getAllIndoFromDb(response, pageNumber)
                }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            users -> callback.onResult(users, 0, pageNumber+1)
                        },
                        {
                             networkState.postValue(NetworkState.ERROR)
                        }
                    )

            }
        })
    }

    fun getAllIndoFromDb(response: Response<UserResponse>, integral: Int): List<User>{
        val users = response.body()!!.user
        if (response.body()!!.meta.totalCount >= integral)
            userInfoDatabase.userDao().insert(users)
        else
            networkState.postValue(NetworkState.EndOfList)


        return getUserInfoFromDb()
    }

    fun getUserInfoFromDb(): List<User>{
        return userInfoDatabase.userDao().getAllUsers()
    }

    fun fetchCallback(users: Any, isConnected: Boolean,
                      callback: LoadInitialCallback<Int, User>){
        if (isConnected)
            fetchInitialData(page, callback)
        else
            callback.onResult(users as MutableList<User>, 0, page)
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }


}