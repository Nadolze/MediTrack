package com.meditrack.user.api.dto;

import com.meditrack.user.domain.entity.User;

/**
 * DTO für die Ausgabe eines Benutzers an den Client.
 *
 * Enthält nur die Felder, die extern sichtbar sein sollen.
 */
public class UserResponse {

    public String id;
    public String name;
    public String email;

    public UserResponse(User user) {
        this.id = user.getId().getValue();
        this.name = user.getName();
        this.email = user.getEmail();
    }
}
