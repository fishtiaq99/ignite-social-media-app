package com.ignite.service;

import com.ignite.model.User;
import com.ignite.model.Post;
import com.ignite.service.search.*;
import java.util.List;

public class SearchService {
    private UserService userService;
    private PostService postService;
    private FollowService followService;
    private SearchContext searchContext;

    public SearchService() {
        this.userService = new UserService();
        this.postService = new PostService();
        this.followService = new FollowService();
        this.searchContext = new SearchContext();
    }

    public SearchStrategy.SearchResult searchAll(String query, int currentUserId) {
        if (query == null || query.trim().isEmpty()) {
            return new SearchStrategy.SearchResult();
        }

        String cleanQuery = query.trim();

        System.out.println("Processing search query: " + cleanQuery); // Debug

        // Determine search type and set appropriate strategy
        if (cleanQuery.startsWith("#")) {
            System.out.println("Detected hashtag search"); // Debug
            // Hashtag search with like-based sorting
            SortingStrategy likeSorting = new LikeRateSortingStrategy(postService);
            SearchStrategy hashtagStrategy = new HashtagSearchStrategy(postService, userService, likeSorting);
            searchContext.setSearchStrategy(hashtagStrategy);
        } else {
            System.out.println("Detected user search"); // Debug
            // User search
            SearchStrategy userStrategy = new UserSearchStrategy(userService, followService);
            searchContext.setSearchStrategy(userStrategy);
        }

        return searchContext.executeSearch(cleanQuery, currentUserId);
    }

    // Helper method to check if user is following another user
    public boolean isFollowing(int followerId, int followeeId) {
        return followService.isFollowing(followerId, followeeId);
    }

    // Helper method to toggle follow status
    public boolean toggleFollow(int followerId, int followeeId) {
        if (isFollowing(followerId, followeeId)) {
            return followService.unfollowUser(followerId, followeeId);
        } else {
            return followService.followUser(followerId, followeeId);
        }
    }

    // Getter for PostService
    public PostService getPostService() {
        return postService;
    }

    // Backward compatibility methods
    public List<User> searchUsers(String query) {
        return userService.searchUsers(query);
    }

    public List<Post> searchPosts(String query) {
        return postService.searchPosts(query);
    }
}