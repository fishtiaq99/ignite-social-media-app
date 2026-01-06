package com.ignite.repository;

import com.ignite.model.Follow;
import java.util.List;
import java.util.Optional;

public interface FollowRepository {

    // Basic operations

    Follow save(Follow follow);
    Optional<Follow> findById(int followerId, int followeeId);
    boolean delete(int followerId, int followeeId);

    // Status operations

    boolean acceptFollowRequest(int followerId, int followeeId);
    boolean rejectFollowRequest(int followerId, int followeeId);
    boolean isFollowing(int followerId, int followeeId);
    boolean isFollowRequestPending(int followerId, int followeeId);

    // List operations

    List<Integer> findFollowerIds(int userId);
    List<Integer> findFollowingIds(int userId);
    List<Integer> findPendingFollowerIds(int userId);

    // Count operations

    int getFollowerCount(int userId);
    int getFollowingCount(int userId);
    int getPendingFollowRequestCount(int userId);

    // Bulk operations

    List<Follow> findAllFollows();
    List<Follow> findPendingFollowRequests();
}