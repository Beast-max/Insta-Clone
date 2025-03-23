package com.example.instagramapp.models

data class ReplyModel(
    val id: String = "",         // Unique reply ID
    val commentId: String = "",  // ID of the parent comment
    val userModel: UserModel = UserModel(),
    val text: String = "",       // Reply content
    val createdAt: Long = 0      // Timestamp for sorting
)

