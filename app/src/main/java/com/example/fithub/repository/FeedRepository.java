package com.example.fithub.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.fithub.model.Post;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class FeedRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void getPosts(MutableLiveData<List<Post>> postsLiveData) {

        db.collection("posts")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {

                    if (error != null || value == null) return;

                    List<Post> posts = new ArrayList<>();

                    for (DocumentSnapshot doc : value.getDocuments()) {

                        Post post = doc.toObject(Post.class);
                        if (post == null) continue;

                        post.setPostId(doc.getId());
                        posts.add(post);

                        // 🔥 LOAD USER DATA
                        db.collection("users")
                                .document(post.getUserId())
                                .get()
                                .addOnSuccessListener(userDoc -> {

                                    if (userDoc.exists()) {
                                        post.setUsername(userDoc.getString("username"));
                                        post.setUserImage(userDoc.getString("image"));
                                    }

                                    postsLiveData.setValue(posts);
                                });
                    }
                });
    }

    // LIKE (OK)
    public void likePost(String postId) {

        DocumentReference ref = db.collection("posts").document(postId);

        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(ref);
            long likes = snapshot.getLong("likesCount") != null
                    ? snapshot.getLong("likesCount")
                    : 0;
            transaction.update(ref, "likesCount", likes + 1);
            return null;
        });
    }

    public void toggleLike(String postId, String currentUserId) {

        DocumentReference ref = db.collection("posts").document(postId);

        db.runTransaction(transaction -> {

            DocumentSnapshot snapshot = transaction.get(ref);

            long likes = snapshot.getLong("likesCount");
            List<String> likedBy = (List<String>) snapshot.get("likedBy");

            if (likedBy == null) likedBy = new ArrayList<>();

            if (likedBy.contains(currentUserId)) {
                // UNLIKE
                likedBy.remove(currentUserId);
                transaction.update(ref,
                        "likesCount", likes - 1,
                        "likedBy", likedBy
                );
            } else {
                // LIKE
                likedBy.add(currentUserId);
                transaction.update(ref,
                        "likesCount", likes + 1,
                        "likedBy", likedBy
                );
            }

            return null;
        });
    }


}


