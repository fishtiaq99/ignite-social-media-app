package com.ignite.model;

import java.util.Date;

public class User {

    private int userId;
    private String username;
    private String email;
    private String password;
    private String bio;
    private Date joinDate;     // <-- RESTORED
    private boolean isApproved;
    private boolean isActive;

    // Constructors
    public User() {
        this.isApproved = true; // you can adjust; DB default also applies
        this.isActive = true;
        this.joinDate = new Date(); // current timestamp, but DB will override on insert
    }

    public User(String username, String email, String password, String bio) {
        this();
        this.username = username;
        this.email = email;
        this.password = password;
        this.bio = bio;
    }

    // Getters & Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean isApproved) {
        this.isApproved = isApproved;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", isApproved=" + isApproved +
                ", isActive=" + isActive +
                ", joinDate=" + joinDate +
                '}';
    }
}
