package com.example.instagramapp.views.home.bottomsheet

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.instagramapp.databinding.FragmentCreatePostBinding
import com.example.instagramapp.databinding.FragmentFollowersBinding
import com.example.instagramapp.models.UserModel
import com.example.instagramapp.post.CreatePostViewModel
import com.example.instagramapp.utilities.SessionManager
import com.example.instagramapp.views.home.ProfileViewModel
import com.example.instagramapp.views.home.adapter.UserAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class UserListFragment(private var operation:String,private val function: ()->Unit):BottomSheetDialogFragment() {
    private val viewModel: ProfileViewModel by activityViewModels()
    private lateinit var adapter: UserAdapter
    private lateinit var binding: FragmentFollowersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFollowersBinding.inflate(layoutInflater,container,false)
        if(operation=="FW"){
            binding.tvTitle.text = "Follower"
            fetchFollower()
        }
        else{
            binding.tvTitle.text = "Following"
            fetchFollowing()
        }
        observeViewModel()
        return binding.root
    }
    private fun fetchFollower(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.follower.collect { follower ->
                adapter = UserAdapter(follower.toMutableList()){ id->
                    viewModel.removeFollower(SessionManager.getString(requireContext(),"id")?:"",id)
                    function.invoke()

                }
                binding.rvUsers.adapter = adapter
            }
        }
    }
    private fun observeViewModel(){
        lifecycleScope.launch {
            viewModel.followerOperation.collect{
                if(it){
                    function.invoke()
                }
            }
        }
    }

    private fun fetchFollowing() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.following.collect { following ->
                adapter = UserAdapter(following.toMutableList()){ id->
                    viewModel.removeFollowing(SessionManager.getString(requireContext(),"id")?:"",id)
                }
                binding.rvUsers.adapter = adapter
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}