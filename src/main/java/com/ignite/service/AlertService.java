package com.ignite.service;

import com.ignite.model.SystemAlert;
import com.ignite.model.enums.AlertType;
import com.ignite.repository.AlertRepository;
import com.ignite.repository.SqlAlertRepository;
import java.util.List;
import java.util.Comparator;

public class AlertService {
    private AlertRepository alertRepository;

    public AlertService() {
        this.alertRepository = new SqlAlertRepository();
    }

    public List<SystemAlert> getActiveAlerts() {
        List<SystemAlert> alerts = alertRepository.findActiveAlerts();
        // Sort by most recent first (already done in SQL query, but double-check)
        alerts.sort(Comparator.comparing(SystemAlert::getCreationDate).reversed());
        return alerts;
    }

    public List<SystemAlert> getUrgentAlerts() {
        List<SystemAlert> alerts = alertRepository.findUrgentAlerts();
        alerts.sort(Comparator.comparing(SystemAlert::getCreationDate).reversed());
        return alerts;
    }

    public List<SystemAlert> getAlertsByType(AlertType alertType) {
        List<SystemAlert> alerts = alertRepository.findByType(alertType);
        alerts.sort(Comparator.comparing(SystemAlert::getCreationDate).reversed());
        return alerts;
    }

    // Get all alerts sorted by most recent first
    public List<SystemAlert> getAllAlertsSorted() {
        List<SystemAlert> alerts = alertRepository.findActiveAlerts();
        alerts.sort(Comparator.comparing(SystemAlert::getCreationDate).reversed());
        return alerts;
    }

    // Check if there are any urgent alerts
    public boolean hasUrgentAlerts() {
        return !getUrgentAlerts().isEmpty();
    }

    // Get the most recent alert
    public SystemAlert getMostRecentAlert() {
        List<SystemAlert> alerts = getActiveAlerts();
        return alerts.isEmpty() ? null : alerts.get(0);
    }
}