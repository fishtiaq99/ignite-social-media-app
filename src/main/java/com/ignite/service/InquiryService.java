package com.ignite.service;

import com.ignite.model.Inquiry;
import com.ignite.repository.InquiryRepository;
import com.ignite.repository.SqlInquiryRepository;
import java.util.List;

public class InquiryService {
    private InquiryRepository inquiryRepository;

    public InquiryService() {
        this.inquiryRepository = new SqlInquiryRepository();
    }

    public boolean submitInquiry(int userId, String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Inquiry message cannot be empty");
        }

        Inquiry inquiry = new Inquiry(userId, message.trim());
        return inquiryRepository.save(inquiry) != null;
    }

    public List<Inquiry> getUserInquiries(int userId) {
        return inquiryRepository.findByUserId(userId);
    }

    // NEW: Get inquiries by status
    public List<Inquiry> getUserInquiriesByStatus(int userId, boolean resolved) {
        List<Inquiry> allInquiries = getUserInquiries(userId);
        if (resolved) {
            return allInquiries.stream()
                    .filter(Inquiry::isResolved)
                    .toList();
        } else {
            return allInquiries.stream()
                    .filter(inquiry -> !inquiry.isResolved())
                    .toList();
        }
    }

    // NEW: Check if user has any pending inquiries
    public boolean hasPendingInquiries(int userId) {
        return getUserInquiries(userId).stream()
                .anyMatch(inquiry -> !inquiry.isResolved());
    }

    // NEW: Get inquiry count
    public int getInquiryCount(int userId) {
        return getUserInquiries(userId).size();
    }
}