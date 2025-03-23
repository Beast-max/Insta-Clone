package com.example.instagramapp.views.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instagramapp.databinding.FragmentHomeBinding
import com.example.instagramapp.models.UserModel
import com.example.instagramapp.utilities.SessionManager
import com.example.instagramapp.views.home.adapter.PostAdapter
import com.example.instagramapp.views.home.bottomsheet.CommentBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: PostAdapter
    private val followingList: MutableList<UserModel> = mutableListOf()
    private lateinit var ownUser:UserModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        fetchPost()
        binding.progressBar3.visibility  = View.VISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUser(SessionManager.getString(requireContext(),"id")?:"")
        observeViewModel()
    }
    private fun observeViewModel(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userFlow.collect{
                ownUser = it

            }
        }
        viewLifecycleOwner.lifecycleScope.launch {

            adapter = PostAdapter(mutableListOf(),{ postId, likedBy ->
                 like(postId, likedBy)
             },{following -> followUser(following)},{following -> unFollowUser(following)},{
                 val dialog= CommentBottomSheetFragment(it)
                 dialog.show(childFragmentManager,"")
             })
            binding.rv.layoutManager = LinearLayoutManager(requireContext())
            binding.rv.adapter = adapter
            binding.swipeRefreshLayout.setOnRefreshListener {
                fetchPost()
            }
            viewModel.posts.collect { posts ->
                adapter.updatePosts(posts)
                Log.d("HomeFragment", "Posts: $posts")
                binding.swipeRefreshLayout.isRefreshing = false
                binding.progressBar3.visibility  = View.GONE

            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.operationFollow.collect {
                fetchPost()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.following.collect { following ->
                adapter.updateFollowing(following)
            }
        }
    }

    private fun fetchPost() {
        viewModel.fetchPosts()
        viewModel.fetchFollowing(SessionManager.getString(requireContext(),"id")?:"")
    }
    private fun like(postId:String,likedBy:MutableList<String>){
        viewModel.updateLike(postId,likedBy)
    }

    private fun followUser(user:UserModel){
         viewModel.follow(SessionManager.getString(requireContext(),"id")?:"",user,ownUser)
    }
    private fun unFollowUser(user:UserModel){
        viewModel.unFollow(SessionManager.getString(requireContext(),"id")?:"",user,ownUser)
    }
}