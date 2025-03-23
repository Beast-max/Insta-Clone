package com.example.instagramapp.views.home.bottomsheet

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instagramapp.databinding.FragmentCommentBinding
import com.example.instagramapp.models.CommentModel
import com.example.instagramapp.models.ReplyModel
import com.example.instagramapp.models.UserModel
import com.example.instagramapp.utilities.Constants
import com.example.instagramapp.utilities.Constants.REPLIES
import com.example.instagramapp.utilities.SessionManager
import com.example.instagramapp.views.home.adapter.CommentAdapter
import com.google.android.gms.tasks.Tasks
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.UUID
import kotlin.random.Random

class CommentBottomSheetFragment(private val postId: String) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCommentBinding
    private lateinit var commentAdapter: CommentAdapter
    private val db = FirebaseFirestore.getInstance()
    private lateinit var user: UserModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        commentAdapter = CommentAdapter(mutableListOf()) { commentId, text ->
            addReplyToFirestore(commentId, text)
        }
        binding.recyclerViewComments.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewComments.adapter = commentAdapter

        loadComments()

        binding.btnSendComment.setOnClickListener {
            val text = binding.etComment.text.toString().trim()
            if (text.isNotEmpty()) {
                postComment(text)
                binding.etComment.text.clear()
                binding.etComment.clearFocus()
                hideKeyboard(requireView())
            }
        }
    }
    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    private fun loadComments() {
        db.collection(Constants.POST).document(postId)
            .collection(Constants.COMMENTS)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { commentSnapshot, _ ->
                commentSnapshot?.let {
                    val comments = mutableListOf<CommentModel>()

                    it.documents.forEach { doc ->
                        val comment = doc.toObject(CommentModel::class.java)?.copy(commentId = doc.id)
                        comment?.let { comments.add(it) }
                    }

                    commentAdapter.updateComments(comments) // Update comments first

                    comments.forEachIndexed { index, comment ->
                        db.collection(Constants.REPLIES)
                            .document(comment.commentId).collection(Constants.REPLIESVAL)
                            .addSnapshotListener { replySnapshot, _ ->
                                replySnapshot?.let { replies ->
                                    val replyList = replies.toObjects(ReplyModel::class.java)
                                    comments[index].replies = replyList.toMutableList()
                                    binding.recyclerViewComments.post {
                                        commentAdapter.notifyItemChanged(index) // Efficient UI update
                                    }
                                }
                            }
                    }
                }
            }
    }

    private fun addReplyToFirestore(commentId: String, replyText: String) {
        val db = FirebaseFirestore.getInstance()
        val replyId = UUID.randomUUID().toString().replace("-", "").take(10)
        val userId =
            SessionManager.getString(requireContext(), "id") ?: ""  // Replace with FirebaseAuth UID
        db.collection(Constants.USERS).document(userId).get().addOnSuccessListener { userDoc ->
            user = userDoc.toObject(UserModel::class.java)!!
            db.collection(Constants.REPLIES)
                .document(commentId).collection(Constants.REPLIESVAL).document(replyId).set(
                    ReplyModel(
                        id = replyId,
                        commentId = commentId,
                        user,
                        text = replyText,
                        createdAt = System.currentTimeMillis()
                    )
                )
                .addOnSuccessListener {
                    Log.d("Reply", "Reply added successfully")
                }

        }
    }

    private fun postComment(text: String) {
        val userId =
            SessionManager.getString(requireContext(), "id") ?: ""
        db.collection(Constants.USERS).document(userId).get().addOnSuccessListener { userDoc ->
            user = userDoc.toObject(UserModel::class.java)!!

            val commentId =
                db.collection(Constants.POST).document(postId).collection("comments").document().id
            val newComment = CommentModel(commentId, user, text, System.currentTimeMillis())

            db.collection(Constants.POST).document(postId).collection(Constants.COMMENTS)
                .document(commentId)
                .set(newComment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
