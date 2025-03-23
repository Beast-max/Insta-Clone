package com.example.instagramapp.views.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.instagramapp.R
import com.example.instagramapp.databinding.FragmentDashBoardBinding
import com.example.instagramapp.databinding.FragmentProfileBinding
import com.example.instagramapp.utilities.Constants
import com.example.instagramapp.utilities.SessionManager
import com.example.instagramapp.views.home.bottomsheet.UserListFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var userId:String
    private val viewModel:ProfileViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater,container,false)
        observeViewModel()
        userId = SessionManager.getString(requireContext(),"id")?:""
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.logout.setOnClickListener {
            logoutUser()
            viewModel.logOut()
            SessionManager.deleteKey(requireContext(),"id")
        }
        binding.follower.setOnClickListener {
            val dialog = UserListFragment("FW"){
                viewModel.fetchFollower(userId)
            }
            dialog.show(childFragmentManager,"")
        }
        binding.following.setOnClickListener {
            val dialog = UserListFragment("FI"){
                viewModel.fetchFollowing(userId)
            }
            dialog.show(childFragmentManager,"")
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.getUser(userId)
        viewModel.fetchFollower(userId)
        viewModel.fetchFollowing(userId)
    }
    fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
    }
    private fun observeViewModel(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userFlow.collect{
                binding.nameTxt.setText(it.fullName)
                Glide.with(binding.root.context)
                    .load(it.profilePicture)
                    .placeholder(R.drawable.ic_profile)
                    .into(binding.imageView10)
                binding.userName.text = it.fullName
                binding.emailTxt.setText(it.email)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.following.collect { following ->
                binding.following.text = "Following ${following.size}"
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.follower.collect { follower ->
                binding.follower.text = "follower ${follower.size}"
            }
        }
    }
}