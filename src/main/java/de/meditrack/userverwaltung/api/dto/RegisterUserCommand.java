package de.meditrack.userverwaltung.api.dto;

import de.meditrack.userverwaltung.domain.model.UserRole;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RegisterUserCommand {

    private String username;
    private String email;
    private String password;
    private String vorname;
    private String nachname;

    private UserRole role;     // ARZT oder PATIENT
    private LocalDate geburtsdatum; // nur für Patienten
}
