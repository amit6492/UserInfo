package com.example.userinfo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.userinfo.buisness.apiservice.Api
import com.example.userinfo.buisness.apiservice.UserClient
import com.example.userinfo.buisness.db.UserInfoDatabase
import com.example.userinfo.buisness.resources.NetworkState
import com.example.userinfo.view.UserInfoAdapter
import com.example.userinfo.repo.UserInfoRepo
import com.example.userinfo.viewmodel.UserInfoViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    private lateinit var userInfoViewModel: UserInfoViewModel

    lateinit var userInfoRepo: UserInfoRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val api: Api = UserClient.getClient()
        userInfoRepo = UserInfoRepo(this, UserInfoDatabase.getInstance(this)!!, api)

        userInfoViewModel = getVieModel()
        val userInfoAdapter = UserInfoAdapter(this)
        val linearLayoutManager = LinearLayoutManager(this)

        var userInfoList : RecyclerView = findViewById(R.id.user_info_list)

        userInfoList.layoutManager = linearLayoutManager
        userInfoList.setHasFixedSize(true)
        userInfoList.adapter = userInfoAdapter

        userInfoViewModel.userInfoPageList.observe(this, Observer {
            userInfoAdapter.submitList(it)
            Log.d("it size", it.size.toString())
        })

        userInfoViewModel.networkState.observe(this, Observer {
            progressBar.visibility = if (
                userInfoViewModel.isListEmpty() && it == NetworkState.LOADING
            ) View.VISIBLE else View.GONE

            error_text.visibility = if (
                userInfoViewModel.isListEmpty() && it == NetworkState.ERROR
            ) View.VISIBLE else View.GONE

            if(!userInfoViewModel.isListEmpty())
                userInfoAdapter.setNetworkState(it)
        })
    }

    private fun getVieModel(): UserInfoViewModel{
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return UserInfoViewModel(userInfoRepo, application) as T
            }
        })[UserInfoViewModel::class.java]
    }

}
