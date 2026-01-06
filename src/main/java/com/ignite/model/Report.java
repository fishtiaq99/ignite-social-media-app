package com.ignite.model;
//import com.ignite.model.enums.AlertType;
//import com.ignite.model.enums.InquiryStatus;
import com.ignite.model.enums.ReportStatus;


import java.util.Date;

public class Report {
    private int reportId;
    private int reporterUserId;
    private Integer postId;
    private Integer commentId;
    private String reason;
    private Date reportDate;
    private ReportStatus status;
    private User reporter; // Transient field
    private Post post;     // Transient field
    private Comment comment; // Transient field

    // Constructors
    public Report() {
        this.reportDate = new Date();
        this.status = ReportStatus.PENDING;
    }

    public Report(int reporterUserId, String reason) {
        this();
        this.reporterUserId = reporterUserId;
        this.reason = reason;
    }

    public Report(int reporterUserId, Integer postId, Integer commentId, String reason) {
        this(reporterUserId, reason);
        this.postId = postId;
        this.commentId = commentId;
    }

    // Business methods
    public boolean isPostReport() {
        return postId != null;
    }

    public boolean isCommentReport() {
        return commentId != null;
    }

    public void markAsReviewed() {
        this.status = ReportStatus.REVIEWED;
    }

    public void markAsResolved() {
        this.status = ReportStatus.RESOLVED;
    }

    public void markAsDismissed() {
        this.status = ReportStatus.DISMISSED;
    }

    public boolean isPending() {
        return status == ReportStatus.PENDING;
    }

    public boolean isValid() {
        // Either postId or commentId must be provided, but not both
        boolean validTarget = (postId != null && commentId == null) || (postId == null && commentId != null);
        return validTarget && reason != null && !reason.trim().isEmpty();
    }

    // Getters and Setters
    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getReporterUserId() {
        return reporterUserId;
    }

    public void setReporterUserId(int reporterUserId) {
        this.reporterUserId = reporterUserId;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Report{" +
                "reportId=" + reportId +
                ", reporterUserId=" + reporterUserId +
                ", postId=" + postId +
                ", commentId=" + commentId +
                ", reason='" + reason + '\'' +
                ", status=" + status +
                ", reportDate=" + reportDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return reportId == report.reportId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(reportId);
    }
}