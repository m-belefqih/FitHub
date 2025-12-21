package com.example.fithub.repository;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CreatePostRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public void createPost(String content, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {

        String userId = auth.getCurrentUser().getUid();
        String postId = db.collection("posts").document().getId();

        // 📅 Date actuelle → String
        String createdAt = new SimpleDateFormat("dd MMM yyyy | hh:mm a", Locale.getDefault()).format(new Date());

        Map<String, Object> post = new HashMap<>();
        post.put("postId", postId);
        post.put("userId", userId);
        post.put("content", content);

        post.put("mediaUrl", "");
        post.put("mediaType", "");

        post.put("likesCount", 0);
        post.put("likedBy", new ArrayList<String>());
        post.put("commentsCount", 0);

        post.put("createdAt", createdAt);

        db.collection("posts")
                .document(postId)
                .set(post)
                .addOnSuccessListener(unused -> onSuccess.onSuccess(null))
                .addOnFailureListener(onFailure);
    }
}
