package com.example.fithub.model;

import java.util.List;

public class Post {

    // Identité du post
    private String postId;

    // Auteur du post
    private String userId;

    // Contenu du post
    private String content;
    // Média (optionnel)
    private String mediaUrl;      // Firebase Storage URL or Google Drive URL
    private String mediaType;     // "image" | "video" | null

    // Statistiques
    private long likesCount;
    private List<String> likedBy;

    private long commentsCount;

    // AJOUT POUR AFFICHAGE DE POSTS
    private String username;        // Firebase va ignorer ce champ
    private String userImage;       // Firebase va ignorer ce champ

    // Temps
    private String createdAt;

    public Post() {} // Firestore obligatoire

    // Parameterized Constructor without username, userImage and likedBy
    public Post(String postId, String userId, String content, String mediaUrl, String mediaType,
                long likesCount, long commentsCount, String createdAt) {
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.createdAt = createdAt;
    }

    // getters & setters
    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(long likesCount) {
        this.likesCount = likesCount;
    }

    public long getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(long commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public List<String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(List<String> likedBy) {
        this.likedBy = likedBy;
    }
}
