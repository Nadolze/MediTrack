package com.meditrack.user.application.service;

import com.meditrack.user.domain.entity.User;
import com.meditrack.user.domain.valueobject.UserId;
import com.meditrack.user.infrastructure.persistence.JpaUserRepository;
import com.meditrack.user.infrastructure.persistence.UserEntityJpa;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testklasse für den UserApplicationService.
 *
 * Zweck:
 * - Prüft, ob der Service einen neuen Benutzer korrekt erstellt.
 * - Prüft, ob das Repository zum Speichern aufgerufen wird.
 * - Sicherstellt, dass keine technische Logik in der Domäne versteckt wird.
 */
class UserApplicationServiceTest {

    @Test
    void sollteBenutzerSpeichernUndZurueckgeben() {
        // Mock des JPA-Repositories erstellen
        JpaUserRepository repository = mock(JpaUserRepository.class);

        // Service mit Mock initialisieren
        UserApplicationService service = new UserApplicationService(repository);

        // Ausführen: Benutzer erstellen
        User benutzer = service.createUser("Marcell", "marcell@example.com");

        // Überprüfen: Der Benutzer sollte nicht null sein
        assertNotNull(benutzer);

        // Überprüfen: Der Name sollte korrekt gesetzt sein
        assertEquals("Marcell", benutzer.getName());

        // Überprüfen: Die E-Mail sollte korrekt gesetzt sein
        assertEquals("marcell@example.com", benutzer.getEmail());

        // Überprüfen: Das Repository muss 1x aufgerufen worden sein
        verify(repository, times(1)).save(any(UserEntityJpa.class));
    }

    @Test
    void sollteFehlerWerfenWennEmailUngueltig() {
        // Mock erzeugen
        JpaUserRepository repository = mock(JpaUserRepository.class);

        // Service initialisieren
        UserApplicationService service = new UserApplicationService(repository);

        // Ungültige E-Mail testen
        assertThrows(IllegalArgumentException.class, () ->
                service.createUser("TestUser", "ungültig")
        );
    }
}
