package com.ignite.repository;

import com.ignite.model.Hashtag;
import java.util.List;
import java.util.Optional;

public interface HashtagRepository {

    // Basic CRUD operations

    Hashtag save(Hashtag hashtag);
    Optional<Hashtag> findById(int hashtagId);
    Optional<Hashtag> findByPhrase(String phrase);
    List<Hashtag> findAll();
    boolean update(Hashtag hashtag);
    boolean delete(int hashtagId);

    // Search operations

    List<Hashtag> searchByPhrase(String query);
    List<Hashtag> findTrendingHashtags(int limit);

    // Usage operations

    boolean incrementUsageCount(int hashtagId);
    boolean decrementUsageCount(int hashtagId);
    List<Hashtag> findMostUsedHashtags(int limit);

    // Post association operations

    boolean associateHashtagWithPost(int hashtagId, int postId);
    boolean removeHashtagFromPost(int hashtagId, int postId);
    List<Hashtag> findHashtagsByPost(int postId);
    List<Integer> findPostIdsByHashtag(int hashtagId);
}