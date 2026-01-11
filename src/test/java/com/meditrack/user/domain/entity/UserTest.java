package com.meditrack.user.domain.entity;

import com.meditrack.user.domain.valueobject.UserId;
import com.meditrack.user.domain.valueobject.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit-Tests für die Domänen-Entität User (TDD/DDD).
 *
 * Ziel:
 * - User wird standardmäßig mit Rolle PATIENT erstellt
 * - User kann mit expliziter Rolle erstellt werden
 * - Null-Rolle im Konstruktor wirft IllegalArgumentException
 * - changeRole() ändert die Rolle korrekt
 */
class UserTest {

    @Test
    @DisplayName("User wird mit Standardrolle PATIENT erstellt")
    void createUser_defaultRole_shouldBePatient() {
        User user = new User(UserId.generate(), "Marcell", "marcell@example.com");

        assertThat(user.getRole()).isEqualTo(UserRole.PATIENT);
    }

    @Test
    @DisplayName("User kann mit expliziter Rolle erstellt werden")
    void createUser_withExplicitRole_shouldWork() {
        User user = new User(UserId.generate(), "Doc", "doc@example.com", UserRole.STAFF);

        assertThat(user.getRole()).isEqualTo(UserRole.STAFF);
    }

    @Test
    @DisplayName("Konstruktor: Null-Rolle wirft Exception")
    void createUser_roleNull_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () ->
                new User(UserId.generate(), "X", "x@example.com", null)
        );
    }

    @Test
    @DisplayName("changeRole ändert Rolle")
    void changeRole_shouldUpdateRole() {
        User user = new User(UserId.generate(), "Marcell", "marcell@example.com");

        user.changeRole(UserRole.ADMIN);

        assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
    }
}
