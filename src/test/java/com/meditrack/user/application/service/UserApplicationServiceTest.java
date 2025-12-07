package com.meditrack.user.application.service;

import com.meditrack.user.application.dto.UserRegistrationDto;
import com.meditrack.user.domain.entity.User;
import com.meditrack.user.domain.valueobject.UserId;
import com.meditrack.user.infrastructure.persistence.JpaUserRepository;
import com.meditrack.user.infrastructure.persistence.UserEntityJpa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit-Tests für den UserApplicationService.
 *
 * Getestet wird:
 *  - Registrierung eines Benutzers (registerUser)
 *  - Login-Prüfung (login)
 *
 * Es werden keine echten Datenbankzugriffe ausgeführt.
 * Das JpaUserRepository wird mit Mockito gemockt.
 */
@ExtendWith(MockitoExtension.class)
class UserApplicationServiceTest {

    @Mock
    private JpaUserRepository jpaUserRepository;

    @InjectMocks
    private UserApplicationService userApplicationService;

    @Test
    @DisplayName("registerUser erzeugt Domain-User und speichert JPA-Entity")
    void registerUser_shouldCreateDomainUserAndPersistEntity() {
        // Arrange
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("marcell");
        dto.setEmail("marcell@example.com");
        dto.setPassword("geheimespw");

        // save(...) soll einfach das übergebene Entity zurückliefern
        when(jpaUserRepository.save(any(UserEntityJpa.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = userApplicationService.registerUser(dto);

        // Assert Domain-Objekt
        assertThat(result).isNotNull();
        assertThat(result.getId()).isInstanceOf(UserId.class);
        assertThat(result.getName()).isEqualTo("marcell");
        assertThat(result.getEmail()).isEqualTo("marcell@example.com");

        // Assert JPA-Entity, die ans Repository übergeben wurde
        ArgumentCaptor<UserEntityJpa> entityCaptor = ArgumentCaptor.forClass(UserEntityJpa.class);
        verify(jpaUserRepository, times(1)).save(entityCaptor.capture());

        UserEntityJpa savedEntity = entityCaptor.getValue();
        assertThat(savedEntity.getId()).isEqualTo(result.getId().getValue());
        assertThat(savedEntity.getName()).isEqualTo("marcell");
        assertThat(savedEntity.getEmail()).isEqualTo("marcell@example.com");
    }

    @Test
    @DisplayName("login liefert true, wenn Benutzer per E-Mail gefunden wird")
    void login_shouldReturnTrueWhenUserFoundByEmail() {
        // Arrange
        String email = "marcell@example.com";
        String password = "egalImMoment";

        UserEntityJpa entity = new UserEntityJpa("123", "marcell", email);
        when(jpaUserRepository.findByEmail(email)).thenReturn(Optional.of(entity));

        // Act
        boolean result = userApplicationService.login(email, password);

        // Assert
        assertThat(result).isTrue();
        verify(jpaUserRepository, times(1)).findByEmail(email);
        verify(jpaUserRepository, never()).findByName(anyString());
    }

    @Test
    @DisplayName("login liefert true, wenn Benutzer per Name gefunden wird")
    void login_shouldReturnTrueWhenUserFoundByName() {
        // Arrange
        String username = "marcell";
        String password = "egalImMoment";

        when(jpaUserRepository.findByEmail(username)).thenReturn(Optional.empty());
        when(jpaUserRepository.findByName(username))
                .thenReturn(Optional.of(new UserEntityJpa("123", username, "marcell@example.com")));

        // Act
        boolean result = userApplicationService.login(username, password);

        // Assert
        assertThat(result).isTrue();
        verify(jpaUserRepository, times(1)).findByEmail(username);
        verify(jpaUserRepository, times(1)).findByName(username);
    }

    @Test
    @DisplayName("login liefert false, wenn kein Benutzer gefunden wird")
    void login_shouldReturnFalseWhenUserNotFound() {
        // Arrange
        String identifier = "unbekannt";
        String password = "egalImMoment";

        when(jpaUserRepository.findByEmail(identifier)).thenReturn(Optional.empty());
        when(jpaUserRepository.findByName(identifier)).thenReturn(Optional.empty());

        // Act
        boolean result = userApplicationService.login(identifier, password);

        // Assert
        assertThat(result).isFalse();
        verify(jpaUserRepository, times(1)).findByEmail(identifier);
        verify(jpaUserRepository, times(1)).findByName(identifier);
    }
}
