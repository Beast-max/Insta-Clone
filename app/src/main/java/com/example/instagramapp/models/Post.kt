package com.example.instagramapp.models



data class Post(var imageUrl: String = "",
                var caption: String = "",
                var likes: Int = 0,
                var comments: Int = 0,
                var timestamp: Long = 0L,
                var likedBy: MutableList<String> = mutableListOf(),
                var user: UserModel = UserModel(), // Ensure UserModel also has a default constructor
                var id: String = "")
