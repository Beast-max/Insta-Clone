package com.example.instagramapp.post

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.instagramapp.R
import com.example.instagramapp.databinding.FragmentCreatePostBinding
import com.example.instagramapp.models.UserModel
import com.example.instagramapp.utilities.SessionManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.util.nextAlphanumericString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.random.Random


@AndroidEntryPoint
class CreatePostFragment : BottomSheetDialogFragment() {
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private lateinit var user:UserModel
    private val createPostViewModel: CreatePostViewModel by viewModels()
    private lateinit var binding: FragmentCreatePostBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreatePostBinding.inflate(layoutInflater,container,false)
        createPostViewModel.getUser(SessionManager.getString(requireContext(),"id")?:"")
        observeViewModel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.createPost.setOnClickListener {
            if(imageUri!=null){
                binding.progressBar.visibility = View.VISIBLE
                uploadImage()
                imageUri = null

            }
        }
        binding.cancel.setOnClickListener {
            dismiss()
        }

        binding.postImage.setOnClickListener { openFileChooser() }
    }

    private fun uploadImage() {
        viewLifecycleOwner.lifecycleScope.launch {
            imageUri?.let { uri ->
                val storageRef = FirebaseStorage.getInstance().reference.child("posts/${System.currentTimeMillis()}.jpg")

                storageRef.putFile(uri)
                    .addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(requireContext(), "Upload successful", Toast.LENGTH_SHORT).show()
                            createPostViewModel.createPost(imageUrl = downloadUrl.toString(), caption = binding.caption.text.toString(),0,0, System.currentTimeMillis(),user,Random.nextAlphanumericString(10))
                            dismiss()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } ?: Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            binding.postImage.setImageURI(imageUri)
        }

    }
    private fun observeViewModel(){
        viewLifecycleOwner.lifecycleScope.launch {
            createPostViewModel.userFlow.collect{
                user = it

            }
        }
    }
    override fun onStart() {
        super.onStart()
        dialog?.let { dialog ->
            val behavior = (dialog as BottomSheetDialog).behavior
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = resources.displayMetrics.heightPixels
        }
    }
    private fun openFileChooser() {
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

}