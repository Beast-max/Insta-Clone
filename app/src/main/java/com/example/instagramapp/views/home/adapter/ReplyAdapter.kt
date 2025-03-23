package com.example.instagramapp.views.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagramapp.R
import com.example.instagramapp.models.ReplyModel

class ReplyAdapter(private val replies: List<ReplyModel>) :
    RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder>() {

    class ReplyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val replyUserProfile: ImageView = view.findViewById(R.id.replyUserProfile)
        val replyUserName: TextView = view.findViewById(R.id.replyUserName)
        val replyText: TextView = view.findViewById(R.id.replyText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_replies, parent, false)
        return ReplyViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReplyViewHolder, position: Int) {
        val reply = replies[position]
        holder.replyUserName.text = reply.userModel.fullName
        holder.replyText.text = reply.text
        Glide.with(holder.itemView.context).load(reply.userModel.profilePicture).into(holder.replyUserProfile)
    }

    override fun getItemCount() = replies.size
}
