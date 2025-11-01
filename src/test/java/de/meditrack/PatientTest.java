package de.meditrack;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

class PatientTest {

    @Test
    void shouldCreatePatient_whenValidDataGiven() {
        Patient p = new Patient("Max Mustermann", "max@example.com", LocalDate.of(1990, 5, 12));
        assertEquals("Max Mustermann", p.getName());
        assertNotNull(p.getId());
    }

    @Test
    void shouldThrowException_whenEmailIsInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
                new Patient("Anna", "keineMail", LocalDate.of(1990, 5, 12)));
    }

    @Test
    void shouldThrowException_whenBirthDateInFuture() {
        assertThrows(IllegalArgumentException.class, () ->
                new Patient("Tom", "tom@example.com", LocalDate.now().plusDays(1)));
    }

    @Test
    void shouldThrowException_whenNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
                new Patient("", "tom@example.com", LocalDate.of(1990, 5, 12)));
    }
}
