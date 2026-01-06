package com.ignite.model;

import java.util.Date;

public class Comment {
    private int commentId;
    private int userId;
    private int postId;
    private String contentText;
    private Date creationDate;
    private User author; // Transient field for UI display
    private Post post;   // Transient field for context

    // Constructors
    public Comment() {
        this.creationDate = new Date();
    }

    public Comment(int userId, int postId, String contentText) {
        this();
        this.userId = userId;
        this.postId = postId;
        this.contentText = contentText;
    }

    // Business methods
    public boolean isEditableBy(User user) {
        return user != null && this.userId == user.getUserId();
    }

    public boolean isValid() {
        return contentText != null && !contentText.trim().isEmpty() && contentText.length() <= 500;
    }

    // Getters and Setters
    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
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

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId=" + commentId +
                ", userId=" + userId +
                ", postId=" + postId +
                ", contentText='" + (contentText != null ? contentText.substring(0, Math.min(30, contentText.length())) + "..." : "null") + '\'' +
                ", creationDate=" + creationDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return commentId == comment.commentId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(commentId);
    }
}