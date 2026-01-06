package com.ignite.model;

import java.util.Date;
import com.ignite.model.enums.AlertType;
//import com.ignite.model.enums.InquiryStatus;
//import com.ignite.model.enums.ReportStatus;

public class SystemAlert {
    private int alertId;
    private int adminId;
    private String message;
    private Date creationDate;
    private AlertType alertType;
    private boolean isActive;
    private String category;

    // Constructors
    public SystemAlert() {
        this.creationDate = new Date();
        this.isActive = true;
        this.alertType = AlertType.ANNOUNCEMENT;
    }

    public SystemAlert(int adminId, String message, AlertType alertType) {
        this();
        this.adminId = adminId;
        this.message = message;
        this.alertType = alertType;
    }


    // Business methods
    public void deactivate() {
        this.isActive = false;
    }

    public boolean isUrgent() {
        return alertType == AlertType.URGENT || alertType == AlertType.SAFETY;
    }

    public boolean isValid() {
        return message != null && !message.trim().isEmpty() && message.length() <= 500;
    }

    // Getters and Setters
    public int getAlertId() {
        return alertId;
    }

    public void setAlertId(int alertId) {
        this.alertId = alertId;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public AlertType getAlertType() {
        return alertType;
    }

    public void setAlertType(AlertType alertType) {
        this.alertType = alertType;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "SystemAlert{" +
                "alertId=" + alertId +
                ", adminId=" + adminId +
                ", message='" + message + '\'' +
                ", alertType=" + alertType +
                ", creationDate=" + creationDate +
                ", active=" + isActive +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.ignite.model.SystemAlert alert = (com.ignite.model.SystemAlert) o;
        return alertId == alert.alertId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(alertId);
    }
}