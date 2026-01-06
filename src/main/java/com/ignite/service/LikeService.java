package com.ignite.service;

import com.ignite.model.Like;
import com.ignite.repository.LikeRepository;
import com.ignite.repository.SqlLikeRepository;

public class LikeService {
    private LikeRepository likeRepository;

    public LikeService() {
        this.likeRepository = new SqlLikeRepository();
    }

    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    public boolean likePost(int userId, int postId) {
        // Check if already liked
        if (likeRepository.isPostLikedByUser(userId, postId)) {
            return true; // Already liked
        }

        Like like = new Like(userId, postId);
        return likeRepository.save(like) != null;
    }

    public boolean unlikePost(int userId, int postId) {
        return likeRepository.deleteByUserAndPost(userId, postId);
    }

    public boolean toggleLike(int userId, int postId) {
        if (likeRepository.isPostLikedByUser(userId, postId)) {
            return unlikePost(userId, postId);
        } else {
            return likePost(userId, postId);
        }
    }

    public boolean isPostLikedByUser(int userId, int postId) {
        return likeRepository.isPostLikedByUser(userId, postId);
    }

    public int getLikeCount(int postId) {
        return likeRepository.getLikeCountByPost(postId);
    }
}