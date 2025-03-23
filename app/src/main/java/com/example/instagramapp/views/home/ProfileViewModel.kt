package com.example.instagramapp.views.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instagramapp.models.UserModel
import com.example.instagramapp.utilities.Constants
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(): ViewModel() {
    private var database = Firebase.firestore
    private val db = FirebaseFirestore.getInstance()

    private val _userFlow = MutableSharedFlow<UserModel>()
    val userFlow = _userFlow

    private val _followerOperation = MutableSharedFlow<Boolean>()
    val followerOperation = _followerOperation

    private val _following = MutableStateFlow<List<UserModel>>(emptyList())
    val following = _following.asStateFlow()

    private val _follower = MutableStateFlow<List<UserModel>>(emptyList())
    val follower = _follower.asStateFlow()

    private val _logOutFlow=  MutableSharedFlow<Boolean>()
    val logOutFlow = _logOutFlow
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
    fun fetchFollower(userId: String) {
        viewModelScope.launch {
            try {
                val snapshot = db.collection(Constants.FOLLOWER).document(userId)
                    .collection(Constants.FOLLOWER)
                    .get()
                    .await()

                val followingList =
                    snapshot.documents.mapNotNull { it.toObject(UserModel::class.java) }
                _follower.emit(followingList)

            } catch (e: Exception) {
                Log.d("set", e.message.toString())
                e.printStackTrace()
                _following.emit(emptyList())
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
    fun removeFollowing(currentUserId:String,userId: String){
            try {
                database.collection(Constants.FOLLOWING).document(currentUserId).collection(Constants.FOLLOWING).document(userId).delete().addOnSuccessListener {
                    viewModelScope.launch {
                        _followerOperation.emit(true)
                    }

                }
                database.collection(Constants.FOLLOWER).document(userId).collection(Constants.FOLLOWER).document(currentUserId).delete().addOnSuccessListener {

                }
            } catch (e: Exception) {
                Log.d("set", e.message.toString())
                e.printStackTrace()
                viewModelScope.launch {
                    _followerOperation.emit(false)
                }
            }

    }
    fun removeFollower(currentUserId:String,userId: String){
            try {
              database.collection(Constants.FOLLOWER).document(currentUserId).collection(Constants.FOLLOWER).document(userId).delete().addOnSuccessListener {
                  viewModelScope.launch {
                      _followerOperation.emit(true)
                  }
                  database.collection(Constants.FOLLOWING).document(userId).collection(Constants.FOLLOWING).document(currentUserId).delete().addOnSuccessListener {

                  }
              }

            } catch (e: Exception) {
                Log.d("set", e.message.toString())
                e.printStackTrace()
                viewModelScope.launch {
                    _followerOperation.emit(false)
            }
        }
    }
    fun logOut() {
      viewModelScope.launch {
          _logOutFlow.emit(true)
      }
    }
}