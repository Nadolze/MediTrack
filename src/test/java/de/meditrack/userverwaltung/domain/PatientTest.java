package de.meditrack.userverwaltung.domain;

import de.meditrack.userverwaltung.domain.model.Patient;
import de.meditrack.userverwaltung.domain.model.UserRole;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PatientTest {

    @Test
    void shouldRejectFutureBirthdate() {
        LocalDate future = LocalDate.now().plusDays(1);
        assertThrows(IllegalArgumentException.class, () ->
                new Patient("mail@example.com", "pass", "user",
                        "Max", "Mustermann", future));
    }

    @Test
    void shouldAcceptValidBirthdate() {
        LocalDate date = LocalDate.of(1990, 1, 1);
        Patient p = new Patient("mail@example.com", "pass", "user",
                "Max", "Mustermann", date);
        assertEquals(date, p.getGeburtsdatum());
        assertEquals(UserRole.PATIENT, p.getRole());
    }
}
