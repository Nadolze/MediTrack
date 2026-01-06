package com.meditrack.user.application.service;

import com.meditrack.shared.valueobject.UserSession;
import com.meditrack.user.application.dto.UserRegistrationDto;
import com.meditrack.user.domain.entity.User;
import com.meditrack.user.infrastructure.persistence.JpaUserRepository;
import com.meditrack.user.infrastructure.persistence.UserEntityJpa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserApplicationServiceTest {

    @Mock
    private JpaUserRepository jpaUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserApplicationService userApplicationService;

    @Test
    @DisplayName("registerUser speichert User als PATIENT inkl. Passwort-Hash")
    void registerUser_shouldSaveEntityWithPatientRole() {
        UserRegistrationDto dto = new UserRegistrationDto(
                "marcell",
                "marcell@example.com",
                "secret123",
                "secret123"
        );

        when(jpaUserRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(jpaUserRepository.findByName(dto.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret123")).thenReturn("HASH");

        User user = userApplicationService.registerUser(dto);

        assertThat(user.getName()).isEqualTo("marcell");
        assertThat(user.getEmail()).isEqualTo("marcell@example.com");

        ArgumentCaptor<UserEntityJpa> captor = ArgumentCaptor.forClass(UserEntityJpa.class);
        verify(jpaUserRepository, times(1)).save(captor.capture());

        UserEntityJpa saved = captor.getValue();
        assertThat(saved.getEmail()).isEqualTo("marcell@example.com");
        assertThat(saved.getPasswordHash()).isEqualTo("HASH");
        assertThat(saved.getRole()).isEqualTo("PATIENT");
    }

    @Test
    @DisplayName("authenticate liefert UserSession bei korrektem Passwort")
    void authenticate_shouldReturnSessionUser() {
        String email = "marcell@example.com";
        String password = "secret123";
        String hash = "HASH";

        UserEntityJpa entity = new UserEntityJpa("123", "marcell", email, hash, "STAFF");

        when(jpaUserRepository.findByEmail(email)).thenReturn(Optional.of(entity));
        when(passwordEncoder.matches(password, hash)).thenReturn(true);

        Optional<UserSession> result = userApplicationService.authenticate(email, password);

        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo("123");
        assertThat(result.get().getUsername()).isEqualTo("marcell");
        assertThat(result.get().getRole()).isEqualTo("STAFF");
    }

    @Test
    @DisplayName("authenticate liefert empty bei falschem Passwort")
    void authenticate_wrongPassword_shouldReturnEmpty() {
        String email = "marcell@example.com";
        String password = "wrong";
        String hash = "HASH";

        UserEntityJpa entity = new UserEntityJpa("123", "marcell", email, hash);

        when(jpaUserRepository.findByEmail(email)).thenReturn(Optional.of(entity));
        when(passwordEncoder.matches(password, hash)).thenReturn(false);

        Optional<UserSession> result = userApplicationService.authenticate(email, password);

        assertThat(result).isEmpty();
        verify(passwordEncoder, times(1)).matches(password, hash);
    }
}
