package com.meditrack.shared.valueobject;

import java.io.Serializable;
import java.util.Objects;

/**
 * Shared-Kernel Principal für die Web-Session.
 *
 * DDD:
 * - User-BC ist Owner von Rollen.
 * - Andere BCs bekommen nur diese schlanke Projektion (read-only).
 */
public final class UserSession implements Serializable {

    private final String userId;
    private final String username;
    private final String email;
    private final String role;

    public UserSession(String userId, String username, String email, String role) {
        this.userId = Objects.requireNonNull(userId, "userId darf nicht null sein");
        this.username = Objects.requireNonNull(username, "username darf nicht null sein");
        this.email = Objects.requireNonNull(email, "email darf nicht null sein");
        this.role = Objects.requireNonNull(role, "role darf nicht null sein");
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public boolean hasRole(String expected) {
        return role.equalsIgnoreCase(expected);
    }

    public boolean hasAnyRole(String... roles) {
        if (roles == null) return false;
        for (String r : roles) {
            if (r != null && hasRole(r)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        // damit Templates einfach ${currentUser} anzeigen können
        return username;
    }
}
