package com.example.instagramapp.views.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.instagramapp.R
import com.example.instagramapp.databinding.FragmentLoginBinding
import com.example.instagramapp.utilities.SessionManager
import com.example.instagramapp.views.signup.RegisterUserViewModel
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.firestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private var mAuth: FirebaseAuth? = null
    private val viewModel: LoginViewModel by viewModels()

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleApiClient: GoogleApiClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.create.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerUserFragment)
        }
        observeViewModel()
        binding.login.setOnClickListener {
            binding.progressBar2.visibility = View.VISIBLE
            loginUserAccount()
        }
        if(SessionManager.getString(requireContext(),"id")!="null"){
            findNavController().navigate(R.id.action_loginFragment_to_dashBoardFragment)
        }
        mAuth = FirebaseAuth.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleApiClient = GoogleApiClient.Builder(requireContext())
            .enableAutoManage(requireActivity()) { /* Handle failure */ }
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        val signinClient = GoogleSignIn.getClient(requireActivity(),gso)
        signinClient.revokeAccess()
        binding.googleLogin.setOnClickListener {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val result = data?.let { Auth.GoogleSignInApi.getSignInResultFromIntent(it) }
            if (result?.isSuccess == true) {
                val account = result.signInAccount
                firebaseAuthWithGoogle(account!!)
            } else {
                Toast.makeText(requireContext(), "Google Sign-In failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)

            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success
//                    val fullName = task.result.user?.displayName
//                    val firstName = fullName?.split(" ")?.getOrNull(0) ?: ""
//                    database.collection(Constants.USERS).document(task.result.user?.email.toString())
//                        .set(
//                            UserModel(task.result.user.uid)
//                        )
//                    val intent = Intent(requireContext(), DashBoardActivity::class.java)
//
//                    startActivity(intent)
//                    activity?.finish()

                } else {
                    // Sign in failed
                    Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.loginUserFlow.collect {
                binding.progressBar2.visibility = View.GONE

                if (it.isSuccess) {
                    SessionManager.saveString(requireContext(), "id", it.id)
                    binding.emailTxt.setText("")
                    binding.passwordTxt.setText("")
                    findNavController().navigate(R.id.action_loginFragment_to_dashBoardFragment)
                }
                else{
                    Toast.makeText(requireContext(), "Email Not Found", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun loginUserAccount() {
        val email: String = binding.emailTxt.getText().toString()
        val password: String = binding.passwordTxt.getText().toString()
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(requireContext(), "Please enter email...", Toast.LENGTH_LONG)
                .show()
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(requireContext(), "Please enter password!", Toast.LENGTH_LONG)
                .show()
            return
        }
        viewModel.loginUserAccount(email, password)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        googleApiClient.stopAutoManage(requireActivity())
        googleApiClient.disconnect()
    // Reset
    }
    override fun onStop() {
        super.onStop()
        if (::googleApiClient.isInitialized && googleApiClient.isConnected) {
            googleApiClient.stopAutoManage(requireActivity())
            googleApiClient.disconnect()
        }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}