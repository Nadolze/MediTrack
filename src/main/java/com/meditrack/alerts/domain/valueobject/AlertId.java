package com.meditrack.alerts.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Eindeutige Alert-ID.
 */
public record AlertId(String value) {

    public AlertId {
        Objects.requireNonNull(value, "AlertId darf nicht null sein.");
        if (value.isBlank()) {
            throw new IllegalArgumentException("AlertId darf nicht leer sein.");
        }
    }

    public static AlertId newId() {
        return new AlertId(UUID.randomUUID().toString());
    }
}
