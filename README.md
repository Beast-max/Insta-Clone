# Insta-CloneInstagram Clone - Android App

This project is an Instagram-like social media application built using Kotlin, Firebase Firestore, and Android Jetpack Components. It includes features such as user authentication, posting images, commenting, replying to comments, real-time updates, and a smooth UI with RecyclerView and BottomSheetDialogFragment.

Features

Authentication & User Management

Firebase Authentication for user sign-up/login.

Store user data in Firestore.

Profile updates and user avatars.

Post & Feed System

Users can upload images with captions.

Display posts in a feed using RecyclerView.

Fetch posts in real-time from Firestore.

Comment System

Users can comment on posts.

Nested replies for engaging discussions.

Real-time updates using Firestore listeners.

Like & Follow System

Users can like posts.

Follow/unfollow users.

Display follower/following count.

User Profile & Settings

View other users' profiles.

Edit profile details.

Change password & logout.

Notification System

Receive notifications for likes/comments/follows.

Search Feature

Search for users by username.

Project Setup Instructions

1. Clone the Repository

git clone <repo-url>
cd InstaGramApp

2. Open in Android Studio

Open Android Studio and select File > Open.

Navigate to the project folder and open it.

3. Connect Firebase

Go to Firebase Console.

Create a new project or use an existing one.

Add Google Services JSON file in app/.

Enable Firestore Database and configure rules.

Enable Firebase Authentication (Email/Google Login).

Enable Firebase Storage for image uploads.

4. Run the App

Ensure Gradle is synced.

Connect a device or use an emulator.

Click Run.

Database Structure (Firestore)

users (Collection)
  ├── userId (Document)
       ├── username: "JohnDoe"
       ├── email: "johndoe@gmail.com"
       ├── profileImage: "url"
       ├── followers: []
       ├── following: []

posts (Collection)
  ├── postId (Document)
       ├── imageUrl: "url"
       ├── caption: "Post Caption"
       ├── userId: "User ID"
       ├── timestamp: Long
       ├── likes: []
       ├── comments (Collection)
            ├── commentId (Document)
                 ├── text: "User Comment"
                 ├── userId: "User ID"
                 ├── timestamp: Long
                 ├── replies (Collection)
                      ├── replyId (Document)
                           ├── text: "User Reply"
                           ├── userId: "User ID"
                           ├── timestamp: Long

Key Components

1. Authentication (Login/Register)

AuthActivity.kt

private fun registerUser(email: String, password: String) {
    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = FirebaseAuth.getInstance().currentUser
                saveUserToDatabase(user)
            }
        }
}

Registers a new user in Firebase Auth.

Saves user details in Firestore.

2. Uploading a Post

PostActivity.kt

private fun uploadPost(imageUri: Uri, caption: String) {
    val postId = UUID.randomUUID().toString()
    val postRef = FirebaseStorage.getInstance().reference.child("posts/$postId.jpg")

    postRef.putFile(imageUri).continueWithTask { task ->
        if (!task.isSuccessful) task.exception?.let { throw it }
        postRef.downloadUrl
    }.addOnSuccessListener { imageUrl ->
        val post = PostModel(postId, imageUrl.toString(), caption, userId, System.currentTimeMillis())
        FirebaseFirestore.getInstance().collection("posts").document(postId).set(post)
    }
}

Uploads image to Firebase Storage.

Saves post details in Firestore.

3. Fetching Posts in Feed

FeedFragment.kt

private fun loadPosts() {
    db.collection("posts").orderBy("timestamp", Query.Direction.DESCENDING)
        .addSnapshotListener { snapshot, _ ->
            snapshot?.let {
                val posts = it.toObjects(PostModel::class.java)
                postAdapter.updatePosts(posts)
            }
        }
}

Fetches posts in real-time and updates the UI.

4. Commenting & Replies

CommentBottomSheetFragment.kt

private fun loadComments() {
    db.collection("posts").document(postId).collection("comments")
        .orderBy("timestamp", Query.Direction.ASCENDING)
        .addSnapshotListener { snapshot, _ ->
            snapshot?.let {
                val comments = it.toObjects(CommentModel::class.java)
                commentAdapter.updateComments(comments)
            }
        }
}

Fetches comments in real-time.

5. Hiding the Keyboard

fun hideKeyboard(view: View) {
    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

Hides the keyboard after posting a comment/reply.

UI Components

RecyclerView for Posts & Comments

BottomSheet for Comments

Profile Page

Search Bar for Users

Add Post Sharing Feature.


