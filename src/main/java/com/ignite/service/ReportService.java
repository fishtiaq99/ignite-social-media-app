package com.ignite.service;

import com.ignite.model.Report;
import com.ignite.model.enums.ReportStatus;
import com.ignite.repository.ReportRepository;
import com.ignite.repository.SqlReportRepository;

import java.util.List;

public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService() {
        this.reportRepository = new SqlReportRepository();
    }

    /**
     * Submit a new report for a post or comment.
     * Only one of postId or commentId must be provided.
     *
     * @param reporterId User ID submitting the report
     * @param postId     Optional post ID to report
     * @param commentId  Optional comment ID to report
     * @param reason     Reason for the report
     * @return true if report is saved successfully
     */
    public boolean submitReport(int reporterId, Integer postId, Integer commentId, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Report reason cannot be empty");
        }

        // Ensure XOR: either postId or commentId must be provided, not both
        if ((postId == null && commentId == null) || (postId != null && commentId != null)) {
            throw new IllegalArgumentException("Provide either a Post ID or a Comment ID, not both.");
        }

        // Prevent duplicate reports
        if (reportRepository.hasUserReportedContent(reporterId, postId, commentId)) {
            throw new IllegalArgumentException("You have already reported this content.");
        }

        Report report = new Report(reporterId, postId, commentId, reason.trim());
        return reportRepository.save(report) != null;
    }

    /**
     * Update the status of a report
     *
     * @param reportId Report ID to update
     * @param status   New status (PENDING, REVIEWED, RESOLVED, DISMISSED)
     * @return true if updated successfully
     */
    public boolean updateReportStatus(int reportId, ReportStatus status) {
        return reportRepository.updateStatus(reportId, status);
    }

    /**
     * Get all reports submitted by a specific user
     *
     * @param userId Reporter user ID
     * @return List of reports
     */
    public List<Report> getUserReports(int userId) {
        return reportRepository.findByReporterId(userId);
    }

    /**
     * Get all pending reports (for admin review)
     *
     * @return List of pending reports
     */
    public List<Report> getPendingReports() {
        return reportRepository.findPendingReports();
    }

    /**
     * Get all reports for a specific post
     *
     * @param postId Post ID
     * @return List of reports
     */
    public List<Report> getPostReports(int postId) {
        return reportRepository.findByPostId(postId);
    }

    /**
     * Get all reports for a specific comment
     *
     * @param commentId Comment ID
     * @return List of reports
     */
    public List<Report> getCommentReports(int commentId) {
        return reportRepository.findByCommentId(commentId);
    }

    /**
     * Get the count of reports for a specific status
     *
     * @param status ReportStatus
     * @return count
     */
    public int getPendingReportCount() {
        return reportRepository.getReportCountByStatus(ReportStatus.PENDING);
    }
}
