package com.ignite.service.search;

import com.ignite.model.User;
import com.ignite.model.Post;
import java.util.List;

public class SearchContext {
    private SearchStrategy searchStrategy;

    public void setSearchStrategy(SearchStrategy searchStrategy) {
        this.searchStrategy = searchStrategy;
    }

    public SearchStrategy.SearchResult executeSearch(String query, int currentUserId) {
        if (searchStrategy == null) {
            throw new IllegalStateException("Search strategy not set");
        }
        return searchStrategy.search(query, currentUserId);
    }
}