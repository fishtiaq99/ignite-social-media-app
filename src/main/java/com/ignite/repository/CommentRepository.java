package com.ignite.repository;

import com.ignite.model.Comment;
import java.util.List;
import java.util.Optional;

public interface CommentRepository {

    // Basic CRUD operations

    Comment save(Comment comment);
    Optional<Comment> findById(int commentId);
    List<Comment> findAll();
    boolean update(Comment comment);
    boolean delete(int commentId);

    // Post-specific operations

    List<Comment> findByPostId(int postId);
    List<Comment> findByUserId(int userId);
    int getCommentCountByPost(int postId);
    int getCommentCountByUser(int userId);

    // Thread operations

    List<Comment> findRecentComments(int limit);
    boolean hasUserCommentedOnPost(int userId, int postId);

    // Search operations

    List<Comment> searchByContent(String query);

    List<Comment> findReportedComments();
    int countAll();
    int countReportedComments();
    boolean adminDeleteComment(int commentId);

    int countByUser(int userId);
    int countByPost(int postId);
}