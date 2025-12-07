package com.meditrack.user.domain.entity;

import com.meditrack.user.domain.valueobject.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit-Tests für die Domänen-Entität User.
 *
 * Getestet wird:
 *  - korrekte Erstellung eines Benutzers
 *  - Validierung im Konstruktor
 *  - Änderung von Name und E-Mail
 */
class UserTest {

    @Test
    @DisplayName("User wird mit gültigen Werten korrekt erstellt")
    void createUser_withValidValues_shouldSucceed() {
        UserId id = UserId.generate();

        User user = new User(id, "Marcell", "marcell@example.com");

        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getName()).isEqualTo("Marcell");
        assertThat(user.getEmail()).isEqualTo("marcell@example.com");
    }

    @Test
    @DisplayName("Konstruktor wirft Exception, wenn UserId null ist")
    void constructor_withNullId_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                new User(null, "Marcell", "marcell@example.com")
        );
    }

    @Test
    @DisplayName("Konstruktor wirft Exception, wenn Name leer ist")
    void constructor_withEmptyName_shouldThrowException() {
        UserId id = UserId.generate();

        assertThrows(IllegalArgumentException.class, () ->
                new User(id, " ", "marcell@example.com")
        );
    }

    @Test
    @DisplayName("Konstruktor wirft Exception, wenn E-Mail ungültig ist")
    void constructor_withInvalidEmail_shouldThrowException() {
        UserId id = UserId.generate();

        assertThrows(IllegalArgumentException.class, () ->
                new User(id, "Marcell", "keine-mail-adresse")
        );
    }

    @Test
    @DisplayName("changeName ändert den Namen des Benutzers")
    void changeName_shouldUpdateName() {
        UserId id = UserId.generate();
        User user = new User(id, "Marcell", "marcell@example.com");

        user.changeName("Neuer Name");

        assertThat(user.getName()).isEqualTo("Neuer Name");
    }

    @Test
    @DisplayName("changeName mit leerem Namen wirft Exception")
    void changeName_withEmptyName_shouldThrowException() {
        UserId id = UserId.generate();
        User user = new User(id, "Marcell", "marcell@example.com");

        assertThrows(IllegalArgumentException.class, () ->
                user.changeName(" ")
        );
    }

    @Test
    @DisplayName("changeEmail ändert die E-Mail-Adresse")
    void changeEmail_shouldUpdateEmail() {
        UserId id = UserId.generate();
        User user = new User(id, "Marcell", "marcell@example.com");

        user.changeEmail("neu@example.com");

        assertThat(user.getEmail()).isEqualTo("neu@example.com");
    }

    @Test
    @DisplayName("changeEmail mit ungültiger E-Mail wirft Exception")
    void changeEmail_withInvalidEmail_shouldThrowException() {
        UserId id = UserId.generate();
        User user = new User(id, "Marcell", "marcell@example.com");

        assertThrows(IllegalArgumentException.class, () ->
                user.changeEmail("ungültig")
        );
    }
}
