package com.example.instagramapp.views.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagramapp.R
import com.example.instagramapp.databinding.ItemPostBinding
import com.example.instagramapp.models.Post
import com.example.instagramapp.models.UserModel
import com.example.instagramapp.utilities.SessionManager
import com.example.instagramapp.views.home.bottomsheet.CommentBottomSheetFragment

class PostAdapter(
    private val postList: MutableList<Post>,
    private val like: (String, MutableList<String>) -> Unit,
    private val function: (user: UserModel) -> Unit,
    private val unFollow: (user: UserModel) -> Unit,
    private val openComment: (postId: String) -> Unit
) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    private val followingList: MutableList<UserModel> = mutableListOf()
    inner class PostViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post,like: (String,MutableList<String>) -> Unit) {

            if(followingList.contains(post.user)){
                binding.btnFollow.text = "UnFollow"
            }else{
                binding.btnFollow.text = "Follow"
            }
            binding.btnComment.setOnClickListener {
                openComment.invoke(post.id)
            }
            binding.btnFollow.setOnClickListener {
                if(followingList.contains(post.user)){
                    followingList.remove(post.user)
                    unFollow.invoke(post.user)
                    binding.btnFollow.text = "Follow"
                }else{
                    followingList.add(post.user)
                    function.invoke(post.user)
                    binding.btnFollow.text = "UnFollow"
                }
            }
            Glide.with(binding.root.context)
                .load(post.user.profilePicture)
                .placeholder(R.drawable.ic_profile)
                .into(binding.imgProfile)

            // Load post image
            Glide.with(binding.root.context)
                .load(post.imageUrl)
                .into(binding.imgPost)

            // Set username and caption
            binding.txtUsername.text = post.user.fullName
            binding.txtCaption.text = post.caption
            binding.txtTimestamp.text = formatTimestamp(post.timestamp)
            binding.txtLikeCount.text = post.likes.toString()
            binding.btnLike.setOnClickListener {
                if(post.likedBy.contains(SessionManager.getString(binding.root.context,"id"))){
                    binding.btnLike.setImageResource(R.drawable.outline_thumb_up_24)
                    post.likedBy.remove(SessionManager.getString(binding.root.context,"id")?:"")
                    post.likes = post.likedBy.size
                    binding.txtLikeCount.text = post.likes.toString()
                    like.invoke(post.id,post.likedBy)

                }
                else{
                    binding.btnLike.setImageResource(R.drawable.baseline_thumb_up_24)
                    post.likedBy.add(SessionManager.getString(binding.root.context,"id")?:"")
                    post.likes = post.likedBy.size
                    binding.txtLikeCount.text = post.likes.toString()
                    like.invoke(post.id,post.likedBy)

                }
            }
            if(post.user.id==(SessionManager.getString(binding.root.context,"id"))){
                binding.btnFollow.visibility = View.GONE
            }
            else{
                binding.btnFollow.visibility = View.VISIBLE
            }
            if(post.likedBy.contains(SessionManager.getString(binding.root.context,"id"))){
                binding.btnLike.setImageResource(R.drawable.baseline_thumb_up_24)
            }
            else{
                binding.btnLike.setImageResource(R.drawable.outline_thumb_up_24)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(postList[position],like)
    }

    override fun getItemCount(): Int = postList.size

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("hh:mm a | dd MMM", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }
    fun updatePosts(newPosts: List<Post>) {
        postList.clear()
        postList.addAll(newPosts)
        notifyDataSetChanged() // Consider using DiffUtil for better performance
    }
    fun updateFollowing(newFollowing: List<UserModel>) {
        followingList.clear()
        followingList.addAll(newFollowing)
        notifyDataSetChanged()
    }
}