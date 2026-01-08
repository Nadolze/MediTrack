package com.meditrack.user.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO für die Registrierung eines neuen Benutzers.
 *
 * Dieses Objekt transportiert die Formulardaten zwischen
 * Controller (API-Schicht) und Application-Service.
 */
public class UserRegistrationDto {

    /**
     * Gewünschter Benutzername.
     */
    @NotBlank(message = "Benutzername darf nicht leer sein.")
    @Size(min = 3, max = 50, message = "Benutzername muss zwischen 3 und 50 Zeichen lang sein.")
    private String username;

    /**
     * E-Mail-Adresse des Benutzers.
     */
    @NotBlank(message = "E-Mail darf nicht leer sein.")
    @Email(message = "Bitte eine gültige E-Mail-Adresse angeben.")
    private String email;

    /**
     * Passwort im Klartext (nur im DTO, niemals speichern!).
     */
    @NotBlank(message = "Passwort darf nicht leer sein.")
    @Size(min = 6, max = 100, message = "Passwort muss mindestens 6 Zeichen lang sein.")
    private String password;

    /**
     * Passwort-Bestätigung.
     */
    @NotBlank(message = "Passwort-Bestätigung darf nicht leer sein.")
    @Size(min = 6, max = 100, message = "Passwort-Bestätigung muss mindestens 6 Zeichen lang sein.")
    private String confirmPassword;

    /**
     * Standard-Konstruktor.
     *
     * Wird von Spring/Thymeleaf benötigt, damit das Objekt
     * als @ModelAttribute gebunden werden kann.
     */
    public UserRegistrationDto() {
    }

    /**
     * Komfort-Konstruktor für Tests und manuelles Erzeugen.
     *
     * @param username         Benutzername
     * @param email            E-Mail
     * @param password         Passwort
     * @param confirmPassword  Passwort-Bestätigung
     */
    public UserRegistrationDto(String username,
                               String email,
                               String password,
                               String confirmPassword) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    // ----- Getter & Setter -----

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
