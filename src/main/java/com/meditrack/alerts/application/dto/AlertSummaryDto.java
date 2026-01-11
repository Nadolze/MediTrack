package com.meditrack.alerts.application.dto;

import com.meditrack.alerts.domain.entity.Alert;
import com.meditrack.alerts.domain.valueobject.AlertStatus;
import com.meditrack.alerts.domain.valueobject.Severity;

import java.time.LocalDateTime;

/**
 * DTO für die Anzeige eines Alerts.
 */
public record AlertSummaryDto(
        String id,
        String patientId,
        String vitalReadingId,
        Severity severity,
        String message,
        AlertStatus status,
        boolean acknowledged,
        boolean resolved,
        LocalDateTime createdAt,
        LocalDateTime resolvedAt
) {
    public static AlertSummaryDto from(Alert alert) {
        boolean acknowledged = alert.getStatus() == AlertStatus.ACKNOWLEDGED || alert.getStatus() == AlertStatus.RESOLVED;
        boolean resolved = alert.getStatus() == AlertStatus.RESOLVED;

        return new AlertSummaryDto(
                alert.getId(),
                alert.getPatientId(),
                alert.getVitalReadingId(),
                alert.getSeverity(),
                alert.getMessage(),
                alert.getStatus(),
                acknowledged,
                resolved,
                alert.getCreatedAt(),
                alert.getResolvedAt()
        );
    }
}
