package com.example.fithub.model;

import java.util.Date;

public class User {

    private String uid;
    private String username;
    private String email;
    private String birthDate;
    private String gender;
    private Date joinTime;
    private String image;
    private String description;
    private double weight;
    private double height;
    private long score;

    // Default Constructor
    public User() {}

    // Parameterized Constructor
    public User(String uid, String username, String email, String birthDate, String gender,
                Date joinTime, String image, String description, double weight, double height, long score) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.birthDate = birthDate;
        this.gender = gender;
        this.joinTime = joinTime;
        this.image = image;
        this.description = description;
        this.weight = weight;
        this.height = height;
        this.score = score;
    }

    // Getters & Setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Date joinTime) {
        this.joinTime = joinTime;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }
}
