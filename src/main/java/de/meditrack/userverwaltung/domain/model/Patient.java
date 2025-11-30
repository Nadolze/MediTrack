package de.meditrack.userverwaltung.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
public class Patient extends User {

    private LocalDate geburtsdatum;

    @ManyToOne
    private Arzt arzt;

    public Patient(
            String email,
            String password,
            String username,
            String vorname,
            String nachname,
            LocalDate geburtsdatum
    ) {
        validateBirthdate(geburtsdatum);

        setEmail(email);
        setPassword(password);
        setUsername(username);
        setVorname(vorname);
        setNachname(nachname);
        setRole(UserRole.PATIENT);

        this.geburtsdatum = geburtsdatum;
    }

    private void validateBirthdate(LocalDate date) {
        if (date == null)
            throw new IllegalArgumentException("Geburtsdatum darf nicht null sein");

        if (date.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Geburtsdatum darf nicht in der Zukunft liegen");
    }

    // 🔥 Diese Methode fehlt bei dir — jetzt ist sie da:
    public void assignToDoctor(Arzt arzt) {
        if (arzt == null) {
            throw new IllegalArgumentException("Arzt darf nicht null sein");
        }

        this.arzt = arzt;
    }
}
