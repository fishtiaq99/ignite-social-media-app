package com.ignite.service.search;

import com.ignite.model.Post;
import java.util.List;

public interface SortingStrategy {
    List<Post> sort(List<Post> posts);
}