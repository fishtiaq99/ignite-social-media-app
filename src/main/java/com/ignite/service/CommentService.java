package com.ignite.service;

import com.ignite.model.Comment;
import com.ignite.model.User;
import com.ignite.repository.CommentRepository;
import com.ignite.repository.SqlCommentRepository;
import java.util.List;

public class CommentService {
    private CommentRepository commentRepository;
    private UserService userService;

    public CommentService() {
        this.commentRepository = new SqlCommentRepository();
        this.userService = new UserService();
    }

    public Comment addComment(int userId, int postId, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content cannot be empty");
        }

        Comment comment = new Comment(userId, postId, content.trim());
        return commentRepository.save(comment);
    }

    public List<Comment> getPostComments(int postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);

        // Load author information for each comment
        for (Comment comment : comments) {
            User author = userService.getUserById(comment.getUserId());
            comment.setAuthor(author);
        }

        return comments;
    }

    public boolean deleteComment(int commentId, int userId) {
        // Check if user owns the comment
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null || comment.getUserId() != userId) {
            return false;
        }
        return commentRepository.delete(commentId);
    }
}