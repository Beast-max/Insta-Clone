package com.example.instagramapp.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instagramapp.models.Post
import com.example.instagramapp.models.UserModel
import com.example.instagramapp.utilities.Constants
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor() : ViewModel() {
    private var database = Firebase.firestore
    private val _userFlow = MutableSharedFlow<UserModel>()
    val userFlow = _userFlow
    fun createPost(
        imageUrl: String,
        caption: String,
        likes: Int,
        comments: Int,
        timestamp: Long,
        user: UserModel,
        id: String
    ) {
        viewModelScope.launch {
            database.collection(Constants.POST  ).document(id)
                .set(
                    Post(
                        id = id,
                        imageUrl = imageUrl,
                        caption = caption,
                        likes = likes,
                        comments = comments,
                        timestamp = timestamp,
                        user = user
                    )
                )
        }
    }

    fun getUser(id: String) {
        database.collection(Constants.USERS).document(id)
            .get()
            .addOnSuccessListener {
                viewModelScope.launch {
                    if(it.exists()){
                        it.toObject(UserModel::class.java)?.let { it1 -> _userFlow.emit(it1) }

                    }
                }

            }
    }
}