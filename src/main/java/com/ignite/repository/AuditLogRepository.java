package com.ignite.repository;

import com.ignite.model.AuditLog;

import java.util.List;

public interface AuditLogRepository {

    /** All audit entries, newest first. */
    List<AuditLog> findAll();

    /** Most recent N entries, newest first. */
    List<AuditLog> findRecent(int limit);
}
