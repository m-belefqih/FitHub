package com.example.fithub.model;

public class Post {

    private String postId;
    private String userId;
    private String username;
    private String content;
    private long timestamp;

    // Obligatoire pour Firebase
    public Post() {}

    public Post(String postId, String userId, String username, String content, long timestamp) {
        this.postId = postId;
        this.userId = userId;
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getPostId() { return postId; }
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }
}
