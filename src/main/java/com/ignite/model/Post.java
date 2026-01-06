package com.ignite.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
//test
public class Post {
    private int postId;
    private int userId;
    private String contentText;
    private String mediaUrl;
    private Date creationDate;
    private Date lastEditedDate;
    private List<Comment> comments;
    private List<Like> likes;
    private List<Hashtag> hashtags;////////////////////////////// maybe aggregate
    private User author; // Transient field for UI display
    private boolean reported;

    // Constructors
    public Post() {
        this.creationDate = new Date();
        this.comments = new ArrayList<>();
        this.likes = new ArrayList<>();
        this.hashtags = new ArrayList<>();
    }

    public Post(int userId, String contentText) {
        this();
        this.userId = userId;
        this.contentText = contentText;
    }

    public Post(int userId, String contentText, String mediaUrl) {
        this(userId, contentText);
        this.mediaUrl = mediaUrl;
    }

    // Business methods
    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void removeComment(Comment comment) {
        this.comments.remove(comment);
    }

    public void addLike(Like like) {
        this.likes.add(like);
    }

    public void removeLike(Like like) {
        this.likes.remove(like);
    }

    public void addHashtag(Hashtag hashtag) {
        this.hashtags.add(hashtag);
    }

    public int getLikeCount() {
        return likes.size();
    }

    public int getCommentCount() {
        return comments.size();
    }

    public boolean isLikedByUser(int userId) {
        return likes.stream().anyMatch(like -> like.getUserId() == userId);
    }

    public boolean isEditableBy(User user) {
        return user != null && this.userId == user.getUserId();
    }

    public void editContent(String newContent) {
        this.contentText = newContent;
        this.lastEditedDate = new Date();
    }

    public boolean hasMedia() {
        return mediaUrl != null && !mediaUrl.trim().isEmpty();
    }

    public List<String> extractHashtags() {
        List<String> hashtags = new ArrayList<>();
        if (contentText != null) {
            String[] words = contentText.split("\\s+");
            for (String word : words) {
                if (word.startsWith("#") && word.length() > 1) {
                    hashtags.add(word.substring(1)); // Remove the # symbol
                }
            }
        }
        return hashtags;
    }

    // Getters and Setters
    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastEditedDate() {
        return lastEditedDate;
    }

    public void setLastEditedDate(Date lastEditedDate) {
        this.lastEditedDate = lastEditedDate;
    }

    public List<Comment> getComments() {
        return new ArrayList<>(comments);
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments != null ? new ArrayList<>(comments) : new ArrayList<>();
    }

    public List<Like> getLikes() {
        return new ArrayList<>(likes);
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes != null ? new ArrayList<>(likes) : new ArrayList<>();
    }

    public List<Hashtag> getHashtags() {
        return new ArrayList<>(hashtags);
    }

    public void setHashtags(List<Hashtag> hashtags) {
        this.hashtags = hashtags != null ? new ArrayList<>(hashtags) : new ArrayList<>();
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Post{" +
                "postId=" + postId +
                ", userId=" + userId +
                ", contentText='" + (contentText != null ? contentText.substring(0, Math.min(50, contentText.length())) + "..." : "null") + '\'' +
                ", creationDate=" + creationDate +
                ", likeCount=" + getLikeCount() +
                ", commentCount=" + getCommentCount() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return postId == post.postId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(postId);
    }

    public boolean isReported() {
        return reported;
    }

    public void setReported(boolean reported) {
        this.reported = reported;
    }
}