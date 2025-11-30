package de.meditrack.userverwaltung.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "patient")
@Getter
@Setter
@NoArgsConstructor  // wichtig für JPA
public class Patient extends User {

    private LocalDate geburtsdatum;

    public Patient(String email,
                   String password,
                   String username,
                   String vorname,
                   String nachname,
                   LocalDate geburtsdatum)
    {
        super(email, password, username, vorname, nachname, UserRole.PATIENT);
        this.geburtsdatum = geburtsdatum;
    }
}
