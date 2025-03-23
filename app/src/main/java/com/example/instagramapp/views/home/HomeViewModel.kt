package com.example.instagramapp.views.home

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.instagramapp.R
import com.example.instagramapp.models.Post
import com.example.instagramapp.models.UserModel
import com.example.instagramapp.utilities.Constants
import com.example.instagramapp.utilities.SessionManager
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private var database = Firebase.firestore
    private val db = FirebaseFirestore.getInstance()
    private val _posts = MutableSharedFlow<List<Post>>()
    val posts = _posts.asSharedFlow()

    private val _following = MutableSharedFlow<List<UserModel>>()
    val following = _following.asSharedFlow()

    private val _operationFollow = MutableSharedFlow<Boolean>()
    val operationFollow = _operationFollow.asSharedFlow()

    private val _userFlow = MutableSharedFlow<UserModel>()
    val userFlow = _userFlow

    fun fetchPosts() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection(Constants.POST)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val postList = snapshot.documents.mapNotNull { it.toObject(Post::class.java) }

                _posts.emit(postList)

            } catch (e: Exception) {
                Log.d("set", e.message.toString())
                e.printStackTrace()
                _posts.emit(emptyList())
            }
        }
    }

    fun fetchFollowing(userId: String) {
        viewModelScope.launch {
            try {
                val snapshot = db.collection(Constants.FOLLOWING).document(userId)
                    .collection(Constants.FOLLOWING)
                    .get()
                    .await()

                val followingList =
                    snapshot.documents.mapNotNull { it.toObject(UserModel::class.java) }
                _following.emit(followingList)

            } catch (e: Exception) {
                Log.d("set", e.message.toString())
                e.printStackTrace()
                _following.emit(emptyList())
            }
        }
    }

    fun updateLike(postId: String, likedBy: List<String>) {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            db.collection(Constants.POST).document(postId)
                .update(mapOf("likes" to likedBy.size, "likedBy" to likedBy))
                .addOnSuccessListener {

                }
                .addOnFailureListener { e ->

                }
        }
    }

    fun follow(userId: String, user: UserModel, ownUser: UserModel) {

        database.collection(Constants.FOLLOWING).document(userId).collection(Constants.FOLLOWING)
            .document(user.id)
            .set(
                user
            )
            .addOnCompleteListener {
                viewModelScope.launch {
                    _operationFollow.emit(true)
                }
            }
        database.collection(Constants.FOLLOWER).document(user.id).collection(Constants.FOLLOWER)
            .document(userId)
            .set(
                ownUser
            )
            .addOnCompleteListener {
                viewModelScope.launch {
                    _operationFollow.emit(true)
                }
            }
    }

    fun unFollow(userId: String, user: UserModel, ownUser: UserModel) {
        viewModelScope.launch {
            database.collection(Constants.FOLLOWING).document(userId)
                .collection(Constants.FOLLOWING).document(user.id).delete()
                .addOnCompleteListener {
                    viewModelScope.launch {
                        _operationFollow.emit(true)
                    }
                }

            database.collection(Constants.FOLLOWER).document(user.id).collection(Constants.FOLLOWER)
                .document(userId).delete()

        }
    }

    fun getUser(id: String) {
        database.collection(Constants.USERS).document(id)
            .get()
            .addOnSuccessListener {
                viewModelScope.launch {
                    if (it.exists()) {
                        it.toObject(UserModel::class.java)?.let { it1 -> _userFlow.emit(it1) }

                    }
                }

            }
    }
}