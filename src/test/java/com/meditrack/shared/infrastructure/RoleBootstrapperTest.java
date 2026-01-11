package com.meditrack.shared.infrastructure;

import com.meditrack.user.infrastructure.persistence.JpaUserRepository;
import com.meditrack.user.infrastructure.persistence.UserEntityJpa;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;

import java.util.Optional;

import static org.mockito.Mockito.*;

class RoleBootstrapperTest {
    /**
     * Smoke- und Verhaltens-Test für den RoleBootstrapper.
     *
     * Ziel:
     * - sicherstellen, dass beim Vorhandensein der Bootstrap-Property
     *   der entsprechende Benutzer gefunden und seine Rolle angepasst wird
     * - verifizieren, dass ohne relevante Properties keinerlei Repository-
     *   Interaktion stattfindet
     * - Absicherung der Startlogik gegen unbeabsichtigte Seiteneffekte
     */
    @Test
    void shouldSetAdminRoleWhenPropertyPresent() {
        JpaUserRepository repo = mock(JpaUserRepository.class);
        Environment env = mock(Environment.class);

        when(env.getProperty("meditrack.bootstrap.admin-email")).thenReturn("admin@example.com");

        UserEntityJpa user = new UserEntityJpa("1", "admin", "admin@example.com", "HASH", "PATIENT");
        when(repo.findByEmail("admin@example.com")).thenReturn(Optional.of(user));

        RoleBootstrapper bootstrapper = new RoleBootstrapper(repo, env);
        bootstrapper.run();

        verify(repo, times(1)).save(argThat(u -> "ADMIN".equalsIgnoreCase(u.getRole())));
    }

    @Test
    void shouldDoNothingWhenPropertyMissing() {
        JpaUserRepository repo = mock(JpaUserRepository.class);
        Environment env = mock(Environment.class);

        when(env.getProperty("meditrack.bootstrap.admin-email")).thenReturn(null);
        when(env.getProperty("meditrack.bootstrap.staff-email")).thenReturn(null);

        RoleBootstrapper bootstrapper = new RoleBootstrapper(repo, env);
        bootstrapper.run();

        verifyNoInteractions(repo);
    }
}
