package com.ignite.service;

import com.ignite.model.Post;
import com.ignite.model.User;
import com.ignite.repository.FollowRepository;
import com.ignite.repository.PostRepository;
import com.ignite.repository.SqlFollowRepository;
import com.ignite.repository.SqlPostRepository;

import java.util.ArrayList;
import java.util.List;

public class PostService {

    private PostRepository postRepository;
    private FollowRepository followRepository;
    private UserService userService;

    public PostService() {
        this.postRepository = new SqlPostRepository();
        this.followRepository = new SqlFollowRepository();
        this.userService = new UserService();
    }

    public PostService(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.followRepository = new SqlFollowRepository();
        this.userService = userService;
    }

    // -------------------------------------------------
    //                     CREATE POST
    // -------------------------------------------------
    public Post createPost(int userId, String content, String mediaUrl) {
        if (content == null || content.trim().isEmpty()) {
            return null;
        }

        Post post = new Post(userId, content.trim(), mediaUrl);
        return postRepository.save(post);  // returns Post, not boolean
    }

    // -------------------------------------------------
    //                     GET POST BY ID
    // -------------------------------------------------
    public Post getPostById(int postId) {
        return postRepository.findById(postId).orElse(null);
    }

    // -------------------------------------------------
    //                        DELETE POST
    // -------------------------------------------------
    public boolean deletePost(int postId, int userId) {
        Post post = getPostById(postId);

        if (post == null) return false;
        if (post.getUserId() != userId) return false;

        return postRepository.delete(postId);
    }

    // -------------------------------------------------
    //                  FEED POSTS
    // -------------------------------------------------
    public List<Post> getPostsFromFollowedUsers(int userId) {
        try {
            // Get actual following users (not from FollowRepository)
            List<User> followingUsers = userService.getFollowing(userId);

            if (followingUsers == null || followingUsers.isEmpty()) {
                return new ArrayList<>();
            }

            // Extract IDs
            List<Integer> followedIds = new ArrayList<>();
            for (User u : followingUsers) {
                followedIds.add(u.getUserId());
            }

            // Get posts
            List<Post> posts = postRepository.findByUserIds(followedIds);

            // Attach author information
            for (Post post : posts) {
                User author = userService.getUserById(post.getUserId());
                post.setAuthor(author);
            }

            return posts;

        } catch (Exception e) {
            System.err.println("Error loading feed: " + e.getMessage());
            return new ArrayList<>();
        }
    }


    // -------------------------------------------------
    //                USER'S OWN POSTS
    // -------------------------------------------------
    public List<Post> getPostsByUser(int userId) {
        try {
            return postRepository.findByUserId(userId);
        } catch (Exception e) {
            System.err.println("Error loading user's posts: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // -------------------------------------------------
    //                TRENDING POSTS
    // -------------------------------------------------
    public List<Post> getTrendingPosts() {
        return postRepository.findTrendingPosts(24, 10);
    }

    // -------------------------------------------------
    //               SEARCH POSTS
    // -------------------------------------------------
    public List<Post> searchPosts(String query) {
        if (query == null || query.trim().isEmpty()) return new ArrayList<>();
        return postRepository.searchByContent(query.trim());
    }

    public PostRepository getPostRepository() {
        return postRepository;
    }

    public List<Post> findByHashtag(String hashtag) {
        if (hashtag == null || hashtag.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // Remove # if user types "#example"
        if (hashtag.startsWith("#")) {
            hashtag = hashtag.substring(1);
        }

        return postRepository.findByHashtag(hashtag.trim());
    }

}
