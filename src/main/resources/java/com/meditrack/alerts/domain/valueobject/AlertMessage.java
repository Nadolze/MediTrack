package com.meditrack.alerts.domain.valueobject;

public record AlertMessage(String value) {

    public AlertMessage {
        if (value == null) {
            throw new IllegalArgumentException("AlertMessage darf nicht null sein.");
        }

        value = value.trim();

        if (value.isBlank()) {
            throw new IllegalArgumentException("AlertMessage darf nicht leer sein.");
        }

        if (value.length() > 300) {
            throw new IllegalArgumentException("AlertMessage darf maximal 300 Zeichen lang sein.");
        }
    }
}
