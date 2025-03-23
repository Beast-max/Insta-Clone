package com.example.instagramapp.views.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagramapp.R
import com.example.instagramapp.models.UserModel

class UserAdapter(private val userList: MutableList<UserModel>,private val function: (id:String)->Unit) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        val imgProfile: ImageView = itemView.findViewById(R.id.imgProfile)
        val remove: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_follower, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.tvName.text = user.fullName
        holder.tvEmail.text = user.email
        holder.remove.setOnClickListener{
            userList.remove(user)
            function.invoke(user.id)
            notifyDataSetChanged()
        }
        Glide.with(holder.itemView.context)
            .load(user.profilePicture)
            .placeholder(R.drawable.ic_profile)
            .into(holder.imgProfile)
    }

    override fun getItemCount() = userList.size
}
