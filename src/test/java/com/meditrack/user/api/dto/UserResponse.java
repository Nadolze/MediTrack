package com.meditrack.user.api.dto;

import com.meditrack.user.domain.entity.User;

import java.util.Objects;

/**
 * DTO für API-Antworten.
 * In deinen Tests werden Felder direkt gelesen (dto.id / dto.name / dto.email / dto.role),
 * daher sind sie public final.
 */
public class UserResponse {

    public final String id;
    public final String name;
    public final String email;
    public final String role;

    public UserResponse(User user) {
        if (user == null) {
            throw new IllegalArgumentException("user must not be null");
        }

        // Wichtig: id über toString() -> wenn UserId.toString() den Wert liefert, passt's sauber.
        this.id = Objects.requireNonNull(user.getId(), "user.id must not be null").toString();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole() == null ? null : user.getRole().name();
    }
}
