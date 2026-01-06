package com.ignite.repository;

import com.ignite.model.enums.AlertType;
import java.util.List;
import java.util.Optional;

import com.ignite.model.SystemAlert;

public interface AlertRepository {

    // Basic CRUD operations

    com.ignite.model.SystemAlert save(com.ignite.model.SystemAlert alert);
    Optional<com.ignite.model.SystemAlert> findById(int alertId);
    List<com.ignite.model.SystemAlert> findAll();
    boolean update(com.ignite.model.SystemAlert alert);
    boolean delete(int alertId);

    // Status operations

    List<com.ignite.model.SystemAlert> findActiveAlerts();
    List<com.ignite.model.SystemAlert> findInactiveAlerts();
    boolean deactivateAlert(int alertId);
    boolean activateAlert(int alertId);

    // Type-based operations

    List<com.ignite.model.SystemAlert> findByType(AlertType alertType);
    List<com.ignite.model.SystemAlert> findUrgentAlerts();

    // Admin operations

    List<com.ignite.model.SystemAlert> findByAdminId(int adminId);
    int getAlertCountByAdmin(int adminId);

    // Recent operations

    List<com.ignite.model.SystemAlert> findRecentAlerts(int limit);


}