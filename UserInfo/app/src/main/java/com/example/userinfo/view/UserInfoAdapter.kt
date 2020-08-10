package com.example.userinfo.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.userinfo.R
import com.example.userinfo.buisness.resources.NetworkState
import com.example.userinfo.model.User
import kotlinx.android.synthetic.main.network_state_item.view.*
import kotlinx.android.synthetic.main.userinfo_list_item.view.*

class UserInfoAdapter(private val context: Context): PagedListAdapter<User, RecyclerView.ViewHolder>(UserDiffCallback()) {

    val USER_VIEW_TYPE = 1
    val NETWORK_VIEW_TYPE = 2

    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View

        if (viewType == USER_VIEW_TYPE){
            view = layoutInflater.inflate(R.layout.userinfo_list_item, parent, false)
            return UserInfoItemViewHolder(view)
        } else{
            view = layoutInflater.inflate(R.layout.network_state_item, parent, false)
            return NetworkStateItemHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == USER_VIEW_TYPE){
            (holder as UserInfoItemViewHolder).bind(getItem(position))
        } else{
            (holder as NetworkStateItemHolder).bind(networkState)
        }
    }

    private fun hasExtraRow(): Boolean{
        return networkState != null && networkState != NetworkState.LOADED
    }

    class UserDiffCallback: DiffUtil.ItemCallback<User>(){
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

    }

    class UserInfoItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        fun bind(userInfo :User?){
            itemView.first_name_value.text = userInfo?.firstName
            itemView.last_name_value.text = userInfo?.lastName
            itemView.gender_value.text = userInfo?.gender


        }
    }

    class NetworkStateItemHolder(view: View): RecyclerView.ViewHolder(view){
        fun bind(networkState: NetworkState?){
            if (networkState != null){
                when(networkState){
                    NetworkState.LOADING -> itemView.progress_bar_item.visibility = View.VISIBLE
                    NetworkState.EndOfList, NetworkState.ERROR -> {
                        itemView.error_msg_item.visibility = View.VISIBLE
                        itemView.error_msg_item.text = networkState.message
                    }
                    else -> itemView.error_msg_item.visibility = View.GONE
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount -1){
            NETWORK_VIEW_TYPE
        } else{
            USER_VIEW_TYPE
        }
    }

    fun setNetworkState(newNetworkState: NetworkState){
        val prevNetworkState: NetworkState? = this.networkState
        val hadExtraRow: Boolean = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow: Boolean = hasExtraRow()

        if(hadExtraRow != hasExtraRow){
            if(hadExtraRow){
                notifyItemRemoved(super.getItemCount())
            } else{
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && prevNetworkState != newNetworkState){
            notifyItemChanged(itemCount -1)
        }
    }

}