package com.ignite.repository;

import com.ignite.model.Inquiry;
import com.ignite.model.enums.InquiryStatus;
import java.util.List;
import java.util.Optional;

public interface InquiryRepository {

    // Basic CRUD operations

    Inquiry save(Inquiry inquiry);
    Optional<Inquiry> findById(int inquiryId);
    List<Inquiry> findAll();
    boolean update(Inquiry inquiry);
    boolean delete(int inquiryId);

    // Status operations

    List<Inquiry> findByStatus(InquiryStatus status);
    List<Inquiry> findPendingInquiries();
    List<Inquiry> findResolvedInquiries();
    boolean markAsResolved(int inquiryId);

    // User-specific operations

    List<Inquiry> findByUserId(int userId);
    int getInquiryCountByUser(int userId);

    // Recent operations

    List<Inquiry> findRecentInquiries(int limit);

    List<Inquiry> findUnansweredInquiries();
    int countAll();
    int countUnansweredInquiries();
    boolean answerInquiry(int inquiryId, int adminId, String response);




}