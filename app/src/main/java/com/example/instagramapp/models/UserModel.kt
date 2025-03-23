package com.example.instagramapp.models

import android.net.Uri

data class UserModel(val id:String, val fullName:String, val email:String, val createdAt:String, val profilePicture: String,
                     val followersCount:Int, val followingCount:Int) {
    // Required empty constructor for Firebase
    constructor() : this("", "", "", "","", 0, 0)
}
