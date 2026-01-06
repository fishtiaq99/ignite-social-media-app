package com.ignite.model;

import java.util.Date;

public class ResolvedReport {
    private int resolvedId;
    private int reporterUserId;
    private String contentType;   // "POST" or "COMMENT"
    private int contentId;
    private Date reportDate;
    private Date resolvedDate;
    private String reason;
    private Integer reportId;     // optional original reportID

    public int getResolvedId() {
        return resolvedId;
    }

    public void setResolvedId(int resolvedId) {
        this.resolvedId = resolvedId;
    }

    public int getReporterUserId() {
        return reporterUserId;
    }

    public void setReporterUserId(int reporterUserId) {
        this.reporterUserId = reporterUserId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public Date getResolvedDate() {
        return resolvedDate;
    }

    public void setResolvedDate(Date resolvedDate) {
        this.resolvedDate = resolvedDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }
}
