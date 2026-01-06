package com.ignite.service;

import com.ignite.model.Follow;
import com.ignite.model.User;
import com.ignite.repository.SqlFollowRepository;
import com.ignite.repository.SqlUserRepository;

import java.util.ArrayList;
import java.util.List;

public class FollowService {

    private final SqlFollowRepository followRepository;
    private final SqlUserRepository userRepository;

    public FollowService() {
        this.followRepository = new SqlFollowRepository();
        this.userRepository = new SqlUserRepository();
    }

    public boolean followUser(int followerId, int followeeId) {
        if (followerId == followeeId) return false; // prevent self-follow
        if (!userRepository.isFollowing(followerId, followeeId)) {
            return followRepository.save(new Follow(followerId, followeeId)) != null;
        }
        return true;
    }

    public boolean unfollowUser(int followerId, int followeeId) {
        return followRepository.delete(followerId, followeeId);
    }

    public List<User> getFollowers(int userId) {
        List<Integer> followerIds = followRepository.findFollowerIds(userId);
        List<User> followers = new ArrayList<>();
        for (Integer id : followerIds) {
            userRepository.findById(id).ifPresent(followers::add);
        }
        return followers;
    }

    public List<User> getFollowing(int userId) {
        List<Integer> followingIds = followRepository.findFollowingIds(userId);
        List<User> following = new ArrayList<>();
        for (Integer id : followingIds) {
            userRepository.findById(id).ifPresent(following::add);
        }
        return following;
    }

    public int getFollowerCount(int userId) {
        return followRepository.getFollowerCount(userId);
    }

    public int getFollowingCount(int userId) {
        return followRepository.getFollowingCount(userId);
    }

    public boolean isFollowing(int followerId, int followeeId) {
        return followRepository.isFollowing(followerId, followeeId);
    }
}
