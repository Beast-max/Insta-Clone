package com.example.instagramapp.models

data class CommentModel(
    val commentId: String = "",
    val user:UserModel = UserModel(),
    val text: String = "",
    val timestamp: Long = 0,
    val repliesCount: Int = 0,
    var replies: MutableList<ReplyModel> = mutableListOf() // List of replies
)
