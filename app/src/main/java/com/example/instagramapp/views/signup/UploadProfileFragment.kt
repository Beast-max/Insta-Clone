package com.example.instagramapp.views.signup

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.instagramapp.R
import com.example.instagramapp.databinding.FragmentUploadProfileBinding
import com.example.instagramapp.utilities.SessionManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class UploadProfileFragment : Fragment() {
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private lateinit var binding: FragmentUploadProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUploadProfileBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.materialCardView5.setOnClickListener {
            openFileChooser()
        }
        binding.upload.setOnClickListener {
            if (imageUri == null) {
                findNavController().navigate(R.id.action_uploadProfileFragment_to_dashBoardFragment)
            } else {
                uploadImage()
                binding.upload.text = "Uploading..."
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            binding.imageView10.setImageURI(imageUri)
            binding.upload.text = "Upload"
        }

    }
    private fun uploadImage() {
        viewLifecycleOwner.lifecycleScope.launch {
            imageUri?.let { uri ->
                val storageRef = FirebaseStorage.getInstance().reference.child("profile_pictures/${System.currentTimeMillis()}.jpg")

                storageRef.putFile(uri)
                    .addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                            saveUserData(downloadUrl.toString())
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } ?: Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserData(imageUrl: String) {
        lifecycleScope.launch {
            val db = FirebaseFirestore.getInstance()
            val userId = SessionManager.getString(requireContext(), "id") ?: ""
            db.collection("users").document(userId)
                .update("profilePicture", imageUrl)
                .addOnSuccessListener {
                    findNavController().navigate(R.id.action_uploadProfileFragment_to_dashBoardFragment)
                    Toast.makeText(requireContext(), "Profile picture updated!", Toast.LENGTH_SHORT)
                        .show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        requireContext(),
                        "Update failed: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }


    private fun openFileChooser() {
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

}