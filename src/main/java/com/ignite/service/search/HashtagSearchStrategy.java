package com.ignite.service.search;

import com.ignite.model.User;
import com.ignite.model.Post;
import com.ignite.service.PostService;
import com.ignite.service.UserService;
import java.util.List;
import java.util.ArrayList;

public class HashtagSearchStrategy implements SearchStrategy {
    private PostService postService;
    private UserService userService;
    private SortingStrategy sortingStrategy;

    public HashtagSearchStrategy(PostService postService, UserService userService, SortingStrategy sortingStrategy) {
        this.postService = postService;
        this.userService = userService;
        this.sortingStrategy = sortingStrategy;
    }

    @Override
    public SearchResult search(String query, int currentUserId) {
        // Remove # if present and clean the query
        String cleanHashtag = query.replace("#", "").trim();

        if (cleanHashtag.isEmpty()) {
            return new SearchResult();
        }

        System.out.println("Searching for hashtag: " + cleanHashtag); // Debug

        // Use the repository's findByHashtag method
        List<Post> posts = postService.getPostRepository().findByHashtag(cleanHashtag);

        System.out.println("Found " + posts.size() + " posts with hashtag: " + cleanHashtag); // Debug

        // Enhance posts with author information and like counts
        List<Post> enhancedPosts = new ArrayList<>();
        for (Post post : posts) {
            // Set author information
            User author = userService.getUserById(post.getUserId());
            post.setAuthor(author);

            // Get like count and set it (you might want to add a transient field for this)
            int likeCount = postService.getPostRepository().getLikeCount(post.getPostId());
            // You can store this in a transient field or handle in UI

            enhancedPosts.add(post);
        }

        // Apply sorting strategy (by likes)
        List<Post> sortedPosts = sortingStrategy.sort(enhancedPosts);

        return new SearchResult(List.of(), sortedPosts);
    }
}