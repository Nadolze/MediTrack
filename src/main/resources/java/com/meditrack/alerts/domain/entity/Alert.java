package com.meditrack.alerts.domain.entity;

import com.meditrack.alerts.domain.valueobject.AlertId;
import com.meditrack.alerts.domain.valueobject.AlertMessage;
import com.meditrack.alerts.domain.valueobject.AlertStatus;
import com.meditrack.alerts.domain.valueobject.PatientId;
import com.meditrack.alerts.domain.valueobject.Severity;
import com.meditrack.alerts.domain.valueobject.VitalReadingId;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Alert Aggregate Root.
 *
 * Hinweis:
 * In euren SQL-Dumps existiert die Tabelle mt_alert mit u.a. folgenden Spalten:
 * id, patient_id, vital_reading_id, severity, message, status, created_at, resolved_at
 *
 * Daher mappen wir hier bewusst nur diese Spalten.
 */
@Entity
@Table(name = "mt_alert")
public class Alert {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @Column(name = "patient_id", length = 36, nullable = false)
    private String patientId;

    @Column(name = "vital_reading_id", length = 36) // in DB ist das NULLable
    private String vitalReadingId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Severity severity;

    @Column(length = 500, nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private AlertStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    protected Alert() {
        // JPA
    }

    private Alert(
            AlertId id,
            PatientId patientId,
            VitalReadingId vitalReadingId,
            Severity severity,
            AlertMessage message,
            LocalDateTime createdAt
    ) {
        this.id = Objects.requireNonNull(id).value();
        this.patientId = Objects.requireNonNull(patientId).value();
        this.vitalReadingId = (vitalReadingId == null ? null : vitalReadingId.value());
        this.severity = Objects.requireNonNull(severity);
        this.message = Objects.requireNonNull(message).value();
        this.status = AlertStatus.OPEN;
        this.createdAt = Objects.requireNonNull(createdAt);
        this.resolvedAt = null;
    }

    public static Alert trigger(
            AlertId id,
            PatientId patientId,
            VitalReadingId vitalReadingId,
            Severity severity,
            AlertMessage message,
            LocalDateTime createdAt
    ) {
        return new Alert(id, patientId, vitalReadingId, severity, message, createdAt);
    }

    public void acknowledge() {
        if (this.status == AlertStatus.RESOLVED) return;
        this.status = AlertStatus.ACKNOWLEDGED;
    }

    public void resolve(LocalDateTime resolvedAt) {
        this.status = AlertStatus.RESOLVED;
        this.resolvedAt = resolvedAt;
    }

    public String getId() { return id; }
    public String getPatientId() { return patientId; }
    public String getVitalReadingId() { return vitalReadingId; }
    public Severity getSeverity() { return severity; }
    public String getMessage() { return message; }
    public AlertStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
}
