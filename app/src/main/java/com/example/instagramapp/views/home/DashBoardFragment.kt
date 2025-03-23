package com.example.instagramapp.views.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.instagramapp.R
import com.example.instagramapp.databinding.FragmentDashBoardBinding
import com.example.instagramapp.post.CreatePostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashBoardFragment : Fragment() {
    private val viewModel:ProfileViewModel by activityViewModels()

    private lateinit var binding: FragmentDashBoardBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.floatingActionButton.setOnClickListener {
            val bottomSheetFragment = CreatePostFragment()
            bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
        }
        observeViewModel()
        binding.bottomNavigationView.setupWithNavController(navController)
    }
    private fun observeViewModel(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.logOutFlow.collect{
                if (it){
                    findNavController().popBackStack(R.id.loginFragment,false)
                }
            }
        }
    }
}