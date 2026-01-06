package com.ignite.service.search;

import com.ignite.model.User;
import com.ignite.model.Post;
import com.ignite.service.UserService;
import com.ignite.service.FollowService;
import java.util.List;

public class UserSearchStrategy implements SearchStrategy {
    private UserService userService;
    private FollowService followService;

    public UserSearchStrategy(UserService userService, FollowService followService) {
        this.userService = userService;
        this.followService = followService;
    }

    @Override
    public SearchResult search(String query, int currentUserId) {
        List<User> users = userService.searchUsers(query);

        // Enhance users with follow status
        for (User user : users) {
            boolean isFollowing = followService.isFollowing(currentUserId, user.getUserId());
            // We'll store this in a transient field or handle in UI
        }

        return new SearchResult(users, List.of());
    }
}