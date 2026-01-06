package com.ignite.model;
//import com.ignite.model.enums.AlertType;
import com.ignite.model.enums.InquiryStatus;
//import com.ignite.model.enums.ReportStatus;

import java.util.Date;

public class Inquiry {
    private int inquiryId;
    private int userId;
    private String message;
    private Date submitDate;
    private InquiryStatus status;
    private User user; // Transient field

    // Constructors
    public Inquiry() {
        this.submitDate = new Date();
        this.status = InquiryStatus.PENDING;
    }

    public Inquiry(int userId, String message) {
        this();
        this.userId = userId;
        this.message = message;
    }

    // Business methods
    public void markAsResolved() {
        this.status = InquiryStatus.RESOLVED;
    }

    public boolean isResolved() {
        return status == InquiryStatus.RESOLVED;
    }

    public boolean isValid() {
        return message != null && !message.trim().isEmpty() && message.length() <= 1000;
    }

    // Getters and Setters
    public int getInquiryId() {
        return inquiryId;
    }

    public void setInquiryId(int inquiryId) {
        this.inquiryId = inquiryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    public InquiryStatus getStatus() {
        return status;
    }

    public void setStatus(InquiryStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Inquiry{" +
                "inquiryId=" + inquiryId +
                ", userId=" + userId +
                ", message='" + (message != null ? message.substring(0, Math.min(50, message.length())) + "..." : "null") + '\'' +
                ", status=" + status +
                ", submitDate=" + submitDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inquiry inquiry = (Inquiry) o;
        return inquiryId == inquiry.inquiryId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(inquiryId);
    }


    public String getStatusDisplay() {
        return status.toString();
    }


}