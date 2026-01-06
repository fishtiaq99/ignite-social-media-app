package com.ignite.service.search;

import com.ignite.model.User;
import com.ignite.model.Post;
import java.util.List;

public interface SearchStrategy {
    SearchResult search(String query, int currentUserId);

    class SearchResult {
        private List<User> users;
        private List<Post> posts;

        public SearchResult() {
            this.users = List.of();
            this.posts = List.of();
        }

        public SearchResult(List<User> users, List<Post> posts) {
            this.users = users != null ? users : List.of();
            this.posts = posts != null ? posts : List.of();
        }

        // Getters and setters
        public List<User> getUsers() { return users; }
        public void setUsers(List<User> users) { this.users = users; }
        public List<Post> getPosts() { return posts; }
        public void setPosts(List<Post> posts) { this.posts = posts; }
        public boolean hasUsers() { return users != null && !users.isEmpty(); }
        public boolean hasPosts() { return posts != null && !posts.isEmpty(); }
        public boolean isEmpty() { return !hasUsers() && !hasPosts(); }
    }
}