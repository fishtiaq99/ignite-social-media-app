package com.ignite.repository;

import com.ignite.model.Like;
import java.util.List;
import java.util.Optional;

public interface LikeRepository {

    // Basic operations

    Like save(Like like);
    Optional<Like> findById(int likeId);
    boolean delete(int likeId);

    // Post-specific operations

    List<Like> findByPostId(int postId);
    List<Like> findByUserId(int userId);
    Optional<Like> findByUserAndPost(int userId, int postId);

    // Check operations

    boolean existsByUserAndPost(int userId, int postId);
    boolean isPostLikedByUser(int userId, int postId);
    int getLikeCountByPost(int postId);
    int getLikeCountByUser(int userId);

    // Bulk operations

    boolean deleteByUserAndPost(int userId, int postId);
    List<Integer> findLikedPostIdsByUser(int userId);
}