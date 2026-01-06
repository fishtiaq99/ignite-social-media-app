package com.ignite.repository;

import com.ignite.model.Post;
import java.util.List;
import java.util.Optional;

public interface PostRepository {

    // Basic CRUD operations
    Post save(Post post);
    Optional<Post> findById(int postId);
    List<Post> findAll();
    boolean update(Post post);
    boolean delete(int postId);

    // User-specific operations
    List<Post> findByUserId(int userId);
    List<Post> findByUserIds(List<Integer> userIds);
    int getPostCountByUser(int userId);

    // Feed and timeline operations
    List<Post> findHomeFeedPosts(List<Integer> followedUserIds, int limit, int offset);
    List<Post> findTrendingPosts(int hours, int limit);
    List<Post> findRecentPosts(int limit);

    // Search and filter operations
    List<Post> searchByContent(String query);
    List<Post> findByHashtag(String hashtag);
    List<Post> findWithMedia();

    // Engagement operations
    boolean incrementLikeCount(int postId);
    boolean decrementLikeCount(int postId);
    boolean incrementCommentCount(int postId);
    boolean decrementCommentCount(int postId);
    int getLikeCount(int postId);
    int getCommentCount(int postId);

    // Hashtag operations
    boolean addHashtagToPost(int postId, int hashtagId);
    boolean removeHashtagFromPost(int postId, int hashtagId);
    List<String> findPostHashtags(int postId);

    List<Post> findReportedPosts();
    int countAll();
    int countReportedPosts();
    boolean adminDeletePost(int postId);

    int countByUser(int userId);

}