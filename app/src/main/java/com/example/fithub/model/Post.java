package com.example.fithub.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;public class Post {

    // --- Champs qui seront sauvegardés dans Firestore ---
    private String userId;
    private String userName;
    private String userProfileImageUrl;
    private String contentText;
    private String postImageUrl;
    private int likeCount;      // Le compteur de "likes"
    private int commentCount;   // Le compteur de "commentaires"
    private @ServerTimestamp Date timestamp; // Firestore remplira ce champ automatiquement

    // --- Champs utilisés uniquement dans l'application (UI) ---
    @Exclude
    private boolean isLikedByCurrentUser = false;
    @Exclude
    private String postId;

    // --- Constructeurs ---

    // 1. Constructeur vide : OBLIGATOIRE pour que Firestore puisse lire les données
    public Post() { }

    // 2. Le constructeur que votre CreatePostActivity essaie d'appeler
    // Il doit accepter les 7 arguments.
    public Post(String userId, String userName, String userProfileImageUrl, String contentText, String postImageUrl, int likeCount, int commentCount) {
        this.userId = userId;
        this.userName = userName;
        this.userProfileImageUrl = userProfileImageUrl;
        this.contentText = contentText;
        this.postImageUrl = postImageUrl;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }


    // --- Getters et Setters (OBLIGATOIRES pour Firestore) ---

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfileImageUrl() {
        return userProfileImageUrl;
    }

    public void setUserProfileImageUrl(String userProfileImageUrl) {
        this.userProfileImageUrl = userProfileImageUrl;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    // --- Getters et Setters pour les champs non sauvegardés ---

    @Exclude
    public boolean isLikedByCurrentUser() {
        return isLikedByCurrentUser;
    }

    @Exclude
    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        isLikedByCurrentUser = likedByCurrentUser;
    }

    @Exclude
    public String getPostId() {
        return postId;
    }

    @Exclude
    public void setPostId(String postId) {
        this.postId = postId;
    }
}
