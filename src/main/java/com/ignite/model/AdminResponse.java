package com.ignite.model;

import java.util.Date;

public class AdminResponse {
    private int adminId;
    private int inquiryId;
    private String response;
    private Date responseDate;

    // Constructors
    public AdminResponse() {
        this.responseDate = new Date();
    }

    public AdminResponse(int adminId, int inquiryId, String response) {
        this();
        this.adminId = adminId;
        this.inquiryId = inquiryId;
        this.response = response;
    }

    // Getters and Setters
    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public int getInquiryId() {
        return inquiryId;
    }

    public void setInquiryId(int inquiryId) {
        this.inquiryId = inquiryId;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Date getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(Date responseDate) {
        this.responseDate = responseDate;
    }

    @Override
    public String toString() {
        return "AdminResponse{" +
                "adminId=" + adminId +
                ", inquiryId=" + inquiryId +
                ", response='" + response + '\'' +
                ", responseDate=" + responseDate +
                '}';
    }
}