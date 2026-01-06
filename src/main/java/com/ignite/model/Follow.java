package com.ignite.model;

import java.util.Date;

public class Follow {
    private int followerId;
    private int followeeId;
    private Date followDate;
    private boolean isAccepted;
    private User follower; // Transient field for UI display
    private User followee; // Transient field for UI display

    // Constructors
    public Follow() {
        this.followDate = new Date();
        this.isAccepted = true; // Default to accepted for public accounts
    }

    public Follow(int followerId, int followeeId) {
        this();
        this.followerId = followerId;
        this.followeeId = followeeId;
    }

    public Follow(int followerId, int followeeId, boolean isAccepted) {
        this(followerId, followeeId);
        this.isAccepted = isAccepted;
    }

    // Business methods
    public void accept() {
        this.isAccepted = true;
    }

    public void reject() {
        this.isAccepted = false;
    }

    public boolean isPending() {
        return !isAccepted;
    }

    public boolean isActive() {
        return isAccepted;
    }

    // Getters and Setters
    public int getFollowerId() {
        return followerId;
    }

    public void setFollowerId(int followerId) {
        this.followerId = followerId;
    }

    public int getFolloweeId() {
        return followeeId;
    }

    public void setFolloweeId(int followeeId) {
        this.followeeId = followeeId;
    }

    public Date getFollowDate() {
        return followDate;
    }

    public void setFollowDate(Date followDate) {
        this.followDate = followDate;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public User getFollower() {
        return follower;
    }

    public void setFollower(User follower) {
        this.follower = follower;
    }

    public User getFollowee() {
        return followee;
    }

    public void setFollowee(User followee) {
        this.followee = followee;
    }

    @Override
    public String toString() {
        return "Follow{" +
                "followerId=" + followerId +
                ", followeeId=" + followeeId +
                ", isAccepted=" + isAccepted +
                ", followDate=" + followDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Follow follow = (Follow) o;
        return followerId == follow.followerId && followeeId == follow.followeeId;
    }

    @Override
    public int hashCode() {
        return 31 * followerId + followeeId;
    }
}