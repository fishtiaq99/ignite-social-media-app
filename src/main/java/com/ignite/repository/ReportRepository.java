package com.ignite.repository;

import com.ignite.model.Report;
import com.ignite.model.enums.ReportStatus;
import java.util.List;
import java.util.Optional;

public interface ReportRepository {

    // Basic CRUD operations
    Report save(Report report);
    Optional<Report> findById(int reportId);
    List<Report> findAll();
    boolean update(Report report);
    boolean delete(int reportId);

    // Status-based queries
    List<Report> findByStatus(ReportStatus status);
    List<Report> findPendingReports();
    List<Report> findResolvedReports();
    boolean updateStatus(int reportId, ReportStatus status);

    // Content-based queries
    List<Report> findByPostId(int postId);
    List<Report> findByCommentId(int commentId);
    List<Report> findByReporterId(int reporterId);

    // Analytics
    int getReportCountByStatus(ReportStatus status);
    List<Report> findRecentReports(int limit);
    int countPendingReports();

    // Validation
    boolean hasUserReportedContent(int userId, Integer postId, Integer commentId);
}