package com.meditrack.user.domain.entity;

import com.meditrack.user.domain.valueobject.UserId; // Import des richtigen Value-Objekts
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void sollteBenutzerKorrektErstellen() {
        // Vorbereiten (Arrange)
        UserId id = new UserId("123");

        // Ausführen (Act)
        User benutzer = new User(id, "Marcell", "marcell@example.com");

        // Überprüfen (Assert)
        assertEquals("123", benutzer.getId().getValue());
        assertEquals("Marcell", benutzer.getName());
        assertEquals("marcell@example.com", benutzer.getEmail());
    }

    @Test
    void sollteFehlerBeiUngueltigerEmailWerfen() {
        // Überprüfen, ob eine ungültige E-Mail korrekt eine Exception auslöst
        assertThrows(IllegalArgumentException.class, () ->
                new User(new UserId("1"), "Test", "ungueltig")
        );
    }

    @Test
    void sollteNamenAendern() {
        // Vorbereiten
        User benutzer = new User(new UserId("1"), "AlterName", "alt@mail.com");

        // Ausführen
        benutzer.changeName("NeuerName");

        // Überprüfen
        assertEquals("NeuerName", benutzer.getName());
    }
}
