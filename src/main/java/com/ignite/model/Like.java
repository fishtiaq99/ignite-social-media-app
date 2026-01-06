package com.ignite.model;

import java.util.Date;

public class Like {
    private int likeId;
    private int userId;
    private int postId;
    private Date likeDate;
    private boolean likedFlag;
    private User user;   // Transient field for UI display
    private Post post;   // Transient field for context

    // Constructors
    public Like() {
        this.likeDate = new Date();
        this.likedFlag = true;
    }

    public Like(int userId, int postId) {
        this();
        this.userId = userId;
        this.postId = postId;
    }

    // Business methods
    public void toggle() {
        this.likedFlag = !this.likedFlag;
        this.likeDate = new Date();
    }

    public boolean isActive() {
        return likedFlag;
    }

    // Getters and Setters
    public int getLikeId() {
        return likeId;
    }

    public void setLikeId(int likeId) {
        this.likeId = likeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public Date getLikeDate() {
        return likeDate;
    }

    public void setLikeDate(Date likeDate) {
        this.likeDate = likeDate;
    }

    public boolean isLikedFlag() {
        return likedFlag;
    }

    public void setLikedFlag(boolean likedFlag) {
        this.likedFlag = likedFlag;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @Override
    public String toString() {
        return "Like{" +
                "likeId=" + likeId +
                ", userId=" + userId +
                ", postId=" + postId +
                ", likedFlag=" + likedFlag +
                ", likeDate=" + likeDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Like like = (Like) o;
        return likeId == like.likeId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(likeId);
    }
}