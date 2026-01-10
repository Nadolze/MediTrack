package com.meditrack.user.domain.valueobject;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object für User-IDs.
 * - Unterstützt generate() für BC (wird im UserApplicationService genutzt)
 * - Unterstützt random()/newId() als Aliase (falls alte Tests/Code das erwartet)
 * - toString() gibt den String-Wert zurück (damit DTO-Mapping/Tests passen)
 */
public final class UserId implements Serializable {

    private final String value;

    private UserId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("UserId must not be null/blank");
        }
        this.value = value;
    }

    /** BC: wird aktuell von UserApplicationService verwendet */
    public static UserId generate() {
        return new UserId(UUID.randomUUID().toString());
    }

    /** Alias (falls irgendwo im Code/Tests genutzt) */
    public static UserId random() {
        return generate();
    }

    /** Alias (falls irgendwo im Code/Tests genutzt) */
    public static UserId newId() {
        return generate();
    }

    public static UserId from(String raw) {
        return new UserId(raw);
    }

    /** Accessor (falls Tests value() erwarten) */
    public String value() {
        return value;
    }

    /** Optional für klassische Beans */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserId userId)) return false;
        return value.equals(userId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
