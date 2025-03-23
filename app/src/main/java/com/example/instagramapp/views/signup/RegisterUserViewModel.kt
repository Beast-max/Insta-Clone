package com.example.instagramapp.views.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instagramapp.models.UserModel
import com.example.instagramapp.network.RegisterUser
import com.example.instagramapp.utilities.Constants
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterUserViewModel @Inject constructor() : ViewModel() {
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _registerUserFlow = MutableSharedFlow<RegisterUser>()
    val registerUserFlow = _registerUserFlow
    private var database = Firebase.firestore

    fun registerNewUser(email: String, password: String) {
        viewModelScope.launch {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    viewModelScope.launch {
                        if (task.isSuccessful) {
                            _registerUserFlow.emit(
                                RegisterUser(
                                    isSuccess = true,
                                    id = task.result.user?.uid!!
                                )
                            )
                        } else {
                            Log.d("RegisterUserViewModel", "registerNewUser: ${task.exception}")
                            _registerUserFlow.emit(
                                RegisterUser(
                                    isSuccess = false,
                                    id = "",
                                    msg = task.exception?.message!!
                                )
                            )
                        }
                    }
                }
        }
    }

    fun createUser(
        id: String, fullName: String, email: String, createdAt: String, image: String,
        followersCount: Int, followingCount: Int
    ) {
        viewModelScope.launch {
            database.collection(Constants.USERS).document(id)
                .set(
                    UserModel(
                        id = id,
                        fullName = fullName,
                        email = email,
                        createdAt = createdAt,
                        profilePicture = image,
                        followersCount = followersCount,
                        followingCount = followingCount
                    )
                )
        }
    }
}