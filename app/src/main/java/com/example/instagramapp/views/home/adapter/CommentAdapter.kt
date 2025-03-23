package com.example.instagramapp.views.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagramapp.R
import com.example.instagramapp.models.CommentModel

class CommentAdapter(private val comments: MutableList<CommentModel>,private val param: (String, String) -> Unit) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userProfile: ImageView = view.findViewById(R.id.    imgProfile)
        val userName: TextView = view.findViewById(R.id.userName)
        val commentText: TextView = view.findViewById(R.id.commentText)
        val repliesRecyclerView: RecyclerView = view.findViewById(R.id.repliesRecyclerView)
        val replyLayout: LinearLayout = view.findViewById(R.id.replyLayout)
        val etReply: EditText = view.findViewById(R.id.etReply)
        val btnSendReply: ImageView = view.findViewById(R.id.btnSendReply)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }
    fun updateComments(newComments: List<CommentModel>) {
        comments.clear()
        comments.addAll(newComments)
        notifyDataSetChanged()  // Refresh RecyclerView
    }
    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]

        holder.userName.text = comment.user.fullName
        holder.commentText.text = comment.text
        Glide.with(holder.itemView.context).load(comment.user.profilePicture).into(holder.userProfile)

        // Set replies
        if (comment.replies.isNotEmpty()) {
            holder.repliesRecyclerView.visibility = View.VISIBLE
            holder.repliesRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
            holder.repliesRecyclerView.adapter = ReplyAdapter(comment.replies)

        }

//        // Reply button
//        holder.btnReply.setOnClickListener {
//            holder.replyLayout.visibility = View.VISIBLE
//        }

        // Send reply
        holder.btnSendReply.setOnClickListener {
            val replyText = holder.etReply.text.toString()
            if (replyText.isNotEmpty()) {
                param.invoke(comment.commentId,holder.etReply.text.toString())
            }
        }
    }

    override fun getItemCount() = comments.size
}
