package com.ignite.service.search;

import com.ignite.model.Post;
import com.ignite.service.PostService;
import java.util.List;
import java.util.Comparator;

public class LikeRateSortingStrategy implements SortingStrategy {
    private PostService postService;

    public LikeRateSortingStrategy(PostService postService) {
        this.postService = postService;
    }

    @Override
    public List<Post> sort(List<Post> posts) {
        // Sort by like count in descending order (most likes first)
        return posts.stream()
                .sorted((p1, p2) -> {
                    int likes1 = postService.getPostRepository().getLikeCount(p1.getPostId());
                    int likes2 = postService.getPostRepository().getLikeCount(p2.getPostId());
                    return Integer.compare(likes2, likes1); // Descending order
                })
                .toList();
    }
}