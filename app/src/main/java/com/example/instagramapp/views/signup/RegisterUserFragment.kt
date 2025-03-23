package com.example.instagramapp.views.signup

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
import com.example.instagramapp.databinding.FragmentRegisterUserBinding
import com.example.instagramapp.utilities.SessionManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterUserFragment : Fragment() {
    private lateinit var binding: FragmentRegisterUserBinding
    private val viewModel: RegisterUserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterUserBinding.inflate(layoutInflater)
        binding.createAccount.setOnClickListener {
            binding.progressBar2.visibility = View.VISIBLE
            checkUser()
        }
        binding.login.setOnClickListener {
            findNavController().navigate(R.id.action_registerUserFragment_to_loginFragment)
        }
        observeViewModel()
        return binding.root
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.registerUserFlow.collect {
                binding.progressBar2.visibility = View.GONE
                if (it.isSuccess) {
                    viewModel.createUser(id = it.id, fullName = binding.nameTxt.text.toString(), email = binding.emailTxt.text.toString(), followersCount = 0, followingCount = 0, createdAt = "", image = "")
                    SessionManager.saveString(requireContext(), "id", it.id)
                    findNavController().navigate(R.id.action_registerUserFragment_to_uploadProfileFragment)
                }
                else{
                    Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkUser() {
        val email: String = binding.emailTxt.text.toString()
        val password: String = binding.passwordTxt.text.toString()
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
        viewModel.registerNewUser(email, password)
    }
}