package com.ignite.service;


import com.ignite.model.*;
import com.ignite.repository.AdminRepository;
import com.ignite.repository.SqlAdminRepository;
import com.ignite.repository.SqlUserRepository;
import com.ignite.repository.SqlPostRepository;
import com.ignite.repository.SqlCommentRepository;
import com.ignite.repository.SqlInquiryRepository;
import com.ignite.repository.SqlReportRepository;
import com.ignite.repository.SqlAlertRepository;
import com.ignite.repository.SqlResolvedReportRepository;
import com.ignite.repository.SqlAuditLogRepository;
import com.ignite.util.PasswordUtil;

import java.util.HashSet;
import java.util.Set;

import com.ignite.model.enums.AlertType;



import java.util.ArrayList;
import java.util.List;

import java.util.Collections;
import java.util.List;

public class AdminService {

    private final AdminRepository adminRepository;
    private final SqlUserRepository userRepository;
    private final SqlPostRepository postRepository;
    private final SqlCommentRepository commentRepository;
    private final SqlInquiryRepository inquiryRepository;
    private final SqlReportRepository reportRepository;
    private final SqlAlertRepository alertRepository;
    private final SqlAuditLogRepository auditLogRepository;
    private final SqlResolvedReportRepository resolvedRepo = new SqlResolvedReportRepository();


    public static class DashboardStats {
        public int totalUsers;
        public int unapprovedUsers;
        public int totalPosts;
        public int reportedPosts;
        public int reportedComments;
        public int pendingInquiries;
        public int pendingReports; // <-- add this
    }


    public AdminService() {
        this.adminRepository = new SqlAdminRepository();
        this.userRepository = new SqlUserRepository();
        this.postRepository = new SqlPostRepository();
        this.commentRepository = new SqlCommentRepository();
        this.inquiryRepository = new SqlInquiryRepository();
        this.reportRepository = new SqlReportRepository();
        this.alertRepository = new SqlAlertRepository();
        this.auditLogRepository = new SqlAuditLogRepository();
    }


    public Admin login(String username, String password) {
        Admin admin = adminRepository.findByUsername(username);

        if (admin == null) {
            return null;  // username not found
        }

        if (!PasswordUtil.verify(password, admin.getPassword())) {
            return null; // wrong password
        }

        return admin;
    }

    // ---- new methods used by AdminDashboardController ----

    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();

        stats.totalUsers       = userRepository.countAll();
        stats.unapprovedUsers  = userRepository.countUnapproved();
        stats.totalPosts       = postRepository.countAll();
        stats.reportedPosts    = postRepository.countReportedPosts();
        stats.reportedComments = commentRepository.countReportedComments();
        stats.pendingInquiries = inquiryRepository.countUnansweredInquiries();
        stats.pendingReports   = reportRepository.countPendingReports();

        return stats;
    }

    // ---- Complete User Management ----
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    public List<User> getActiveUsers() {
        return userRepository.findActiveUsers();
    }

    public List<User> getInactiveUsers() {
        return userRepository.findInactiveUsers();
    }


    public boolean deactivateUser(int userId) {
        return userRepository.deactivateUser(userId);
    }

    public boolean activateUser(int userId) {
        return userRepository.activateUser(userId);
    }


    public int getUserCount() {
        return userRepository.countAll();
    }

    public int getUnapprovedUserCount() {
        return userRepository.countUnapproved();
    }

    /** List of users that are not yet approved. */
    public List<User> getUnapprovedUsers() {
        return userRepository.findUnapprovedUsers();
    }

    /** Approve a user account (returns true if successful). */
    public boolean approveUser(int userId) {
        return userRepository.approveUser(userId);
    }

    /** Permanently delete a user (admin action). */
    public boolean deleteUser(int userId) {
        return userRepository.adminDeleteUser(userId);
    }

    // ---- Post Management ----
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public List<Post> getReportedPosts() {
        return postRepository.findReportedPosts();
    }

    public List<Post> getRecentPosts(int limit) {
        return postRepository.findRecentPosts(limit);
    }

    public boolean deletePost(int postId) {
        return postRepository.adminDeletePost(postId);
    }

    public int getTotalPostsCount() {
        return postRepository.countAll();
    }

    public int getReportedPostsCount() {
        return postRepository.countReportedPosts();
    }

    // Get posts with author information + reported flag
    public List<Post> getAllPostsWithAuthors() {
        List<Post> posts = getAllPosts();
        enrichPostsWithAuthorAndReported(posts);
        return posts;
    }

    public List<Post> getReportedPostsWithAuthors() {
        List<Post> posts = getReportedPosts();
        enrichPostsWithAuthorAndReported(posts);
        return posts;
    }

    public List<Post> getRecentPostsWithAuthors(int limit) {
        List<Post> posts = getRecentPosts(limit);
        enrichPostsWithAuthorAndReported(posts);
        return posts;
    }



    // --- internal helper to add author + reported flag to posts ---
    private void enrichPostsWithAuthorAndReported(List<Post> posts) {
        // Build a set of reported post IDs once
        List<Post> reportedPosts = postRepository.findReportedPosts();
        Set<Integer> reportedIds = new HashSet<>();
        for (Post rp : reportedPosts) {
            reportedIds.add(rp.getPostId());
        }

        // Attach author and reported flag
        for (Post post : posts) {
            User author = userRepository.findById(post.getUserId()).orElse(null);
            post.setAuthor(author);
            post.setReported(reportedIds.contains(post.getPostId()));
        }
    }

    // ---- Comment Management ----
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public List<Comment> getReportedComments() {
        return commentRepository.findReportedComments();
    }

    public List<Comment> getRecentComments(int limit) {
        return commentRepository.findRecentComments(limit);
    }

    public boolean deleteComment(int commentId) {
        return commentRepository.adminDeleteComment(commentId);
    }


    public int getTotalCommentsCount() {
        return commentRepository.countAll();
    }

    public int getReportedCommentsCount() {
        return commentRepository.countReportedComments();
    }

    // Get comments with author and post information
    public List<Comment> getAllCommentsWithDetails() {
        List<Comment> comments = getAllComments();
        // Enrich comments with author and post information
        for (Comment comment : comments) {
            User author = userRepository.findById(comment.getUserId()).orElse(null);
            comment.setAuthor(author);

            Post post = postRepository.findById(comment.getPostId()).orElse(null);
            comment.setPost(post);
        }
        return comments;
    }

    public List<Comment> getReportedCommentsWithDetails() {
        List<Comment> comments = getReportedComments();
        // Enrich comments with author and post information
        for (Comment comment : comments) {
            User author = userRepository.findById(comment.getUserId()).orElse(null);
            comment.setAuthor(author);

            Post post = postRepository.findById(comment.getPostId()).orElse(null);
            comment.setPost(post);
        }
        return comments;
    }

    public List<ResolvedReport> getAllResolvedReports() {
        return resolvedRepo.findAll(); // implement this repo
    }

    public List<Inquiry> getUnansweredInquiries() {
        return inquiryRepository.findUnansweredInquiries();
    }

    public List<Inquiry> getResolvedInquiries() {
        return inquiryRepository.findResolvedInquiries();
    }

    public boolean markAsResolved(int inquiryId) {
        return inquiryRepository.markAsResolved(inquiryId);
    }
    // Get inquiries with user information
    public List<Inquiry> getAllInquiriesWithUsers() {
        List<Inquiry> inquiries = getAllInquiries();
        // Enrich inquiries with user information
        for (Inquiry inquiry : inquiries) {
            User user = userRepository.findById(inquiry.getUserId()).orElse(null);
            inquiry.setUser(user);
        }
        return inquiries;
    }

    public List<Inquiry> getUnansweredInquiriesWithUsers() {
        List<Inquiry> inquiries = getUnansweredInquiries();
        // Enrich inquiries with user information
        for (Inquiry inquiry : inquiries) {
            User user = userRepository.findById(inquiry.getUserId()).orElse(null);
            inquiry.setUser(user);
        }
        return inquiries;
    }

    // ---------- INQUIRIES ----------

    public List<Inquiry> getAllInquiries() {
        return inquiryRepository.findAll();
    }

    public List<Inquiry> getPendingInquiries() {
        return inquiryRepository.findPendingInquiries();
    }


    public List<Inquiry> getRecentInquiries(int limit) {
        return inquiryRepository.findRecentInquiries(limit);
    }

    public int getTotalInquiriesCount() {
        return inquiryRepository.countAll();
    }

    public int getUnansweredInquiriesCount() {
        return inquiryRepository.countUnansweredInquiries();
    }

    /**
     * Answer an inquiry and mark it as resolved.
     * adminId should be the currently logged-in admin.
     */
    public boolean answerInquiry(int inquiryId, int adminId, String response) {
        if (response == null || response.trim().isEmpty()) {
            return false;
        }
        return inquiryRepository.answerInquiry(inquiryId, adminId, response.trim());
    }

    // ---- Alert Management ----
    public List<SystemAlert> getAllAlerts() {
        return alertRepository.findAll();
    }

    public List<SystemAlert> getActiveAlerts() {
        return alertRepository.findActiveAlerts();
    }

    public List<SystemAlert> getRecentAlerts(int limit) {
        return alertRepository.findRecentAlerts(limit);
    }

    public List<SystemAlert> getAlertsByType(AlertType alertType) {
        return alertRepository.findByType(alertType);
    }

    public List<SystemAlert> getUrgentAlerts() {
        return alertRepository.findUrgentAlerts();
    }

    public boolean sendAlert(SystemAlert alert) {
        return alertRepository.save(alert) != null;
    }

    public boolean deactivateAlert(int alertId) {
        return alertRepository.deactivateAlert(alertId);
    }

    public boolean deleteAlert(int alertId) {
        return alertRepository.delete(alertId);
    }

    public int getTotalAlertsCount() {
        return alertRepository.findAll().size();
    }

    public int getActiveAlertsCount() {
        return alertRepository.findActiveAlerts().size();
    }

    public int getUrgentAlertsCount() {
        return alertRepository.findUrgentAlerts().size();
    }

    public boolean activateAlert(int alertId) {
        return alertRepository.activateAlert(alertId);
    }

    // ---- Audit Log (read-only) ----

    public java.util.List<AuditLog> getAllAuditLogs() {
        return auditLogRepository.findAll();
    }

    public java.util.List<AuditLog> getRecentAuditLogs(int limit) {
        return auditLogRepository.findRecent(limit);
    }


}
