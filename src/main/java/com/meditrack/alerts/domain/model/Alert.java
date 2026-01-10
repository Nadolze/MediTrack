package com.meditrack.alerts.domain.model;

import com.meditrack.alerts.domain.valueobject.Severity;
import com.meditrack.vitals.domain.valueobject.VitalType;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Alert {

    private final String id;
    private final String patientId;
    private final String vitalReadingId;
    private final VitalType type;
    private final Severity severity;
    private final String message;
    private final Instant createdAt;

    private AlertStatus status;
    private Instant acknowledgedAt;
    private Instant resolvedAt;

    private Alert(
            String id,
            String patientId,
            String vitalReadingId,
            VitalType type,
            Severity severity,
            String message,
            Instant createdAt,
            AlertStatus status,
            Instant acknowledgedAt,
            Instant resolvedAt
    ) {
        this.id = require(id, "id");
        this.patientId = require(patientId, "patientId");
        this.vitalReadingId = require(vitalReadingId, "vitalReadingId");
        this.type = Objects.requireNonNull(type, "type");
        this.severity = Objects.requireNonNull(severity, "severity");
        this.message = require(message, "message");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.status = Objects.requireNonNull(status, "status");
        this.acknowledgedAt = acknowledgedAt;
        this.resolvedAt = resolvedAt;
    }

    public static Alert newAlert(
            String patientId,
            String vitalReadingId,
            VitalType type,
            Severity severity,
            String message,
            Instant createdAt
    ) {
        return new Alert(
                UUID.randomUUID().toString(),
                patientId,
                vitalReadingId,
                type,
                severity,
                message,
                createdAt,
                AlertStatus.OPEN,
                null,
                null
        );
    }

    public void acknowledge(Instant when) {
        Objects.requireNonNull(when, "when");
        if (status == AlertStatus.RESOLVED) {
            return; // bereits erledigt
        }
        this.status = AlertStatus.ACKNOWLEDGED;
        this.acknowledgedAt = when;
    }

    public void resolve(Instant when) {
        Objects.requireNonNull(when, "when");
        this.status = AlertStatus.RESOLVED;
        this.resolvedAt = when;
        if (this.acknowledgedAt == null) {
            this.acknowledgedAt = when;
        }
    }

    // --- Getter (als Methoden, damit DTO::from sauber klappt) ---
    public String id() { return id; }
    public String patientId() { return patientId; }
    public String vitalReadingId() { return vitalReadingId; }
    public VitalType type() { return type; }
    public Severity severity() { return severity; }
    public String message() { return message; }
    public Instant createdAt() { return createdAt; }
    public AlertStatus status() { return status; }
    public Instant acknowledgedAt() { return acknowledgedAt; }
    public Instant resolvedAt() { return resolvedAt; }

    private static String require(String v, String field) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException(field + " darf nicht leer sein.");
        return v.trim();
    }
}
