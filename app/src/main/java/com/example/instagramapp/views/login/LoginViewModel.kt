package com.example.instagramapp.views.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instagramapp.network.RegisterUser
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _loginUserFlow = MutableSharedFlow<RegisterUser>()
     val loginUserFlow = _loginUserFlow

     fun loginUserAccount(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                viewModelScope.launch {
                    if (task.isSuccessful) {
                        _loginUserFlow.emit(
                            RegisterUser(
                                isSuccess = true,
                                id = task.result.user?.uid!!
                            )
                        )
                    } else {
                        _loginUserFlow.emit(
                            RegisterUser(
                                isSuccess = false,
                                id = ""
                            )
                        )
                    }

                }

            }
    }
}