package com.meditrack.user.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * CreateUserRequest: Tests prüfen Annotationen DIREKT AUF FELDERN (Reflection Field#getAnnotation),
 * daher müssen die Constraints auf den Feldern stehen (nicht nur auf Getter/Record-Components).
 */
public class CreateUserRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Email
    @Size(max = 255)
    private String email;

    // Optional (nur falls du es im Projekt brauchst):
    // @NotBlank
    // @Size(min = 6, max = 255)
    // private String password;

    public CreateUserRequest() {
    }

    public CreateUserRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
