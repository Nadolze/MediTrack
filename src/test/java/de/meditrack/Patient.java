package de.meditrack;

import java.time.LocalDate;
import java.util.UUID;

// Nochmal anpassen (Deutsch / Englisch und welche Attribute gibt es noch)

public class Patient {

    private final String id;
    private String name;
    private String email;
    private LocalDate geburtsdatum;

    public Patient(String name, String email, LocalDate geburtsdatum) {
        validateName(name);
        validateEmail(email);
        validateBirthDate(geburtsdatum);

        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.geburtsdatum = geburtsdatum;
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Name darf nicht leer sein.");
    }

    private void validateEmail(String email) {
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
            throw new IllegalArgumentException("Ungültige E-Mail-Adresse.");
    }

    private void validateBirthDate(LocalDate geburtsdatum) {
        if (geburtsdatum.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Geburtsdatum darf nicht in der Zukunft liegen.");
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public LocalDate getGeburtsdatum() { return geburtsdatum; }
}
