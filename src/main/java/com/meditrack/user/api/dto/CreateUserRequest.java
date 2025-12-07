package com.meditrack.user.api.dto;

/**
 * DTO für eingehende Benutzer-Registrierungsdaten.
 *
 * Dieses Objekt enthält nur die Daten, die ein Client
 * beim Erstellen eines neuen Benutzers übermittelt.
 */
public class CreateUserRequest {

    public String name;
    public String email;

    // Standardkonstruktor benötigt für JSON → Objekt-Mapping
    public CreateUserRequest() {
    }
}
