package com.meditrack.user.infrastructure.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integrationstest für das JPA-Repository.
 *
 * Es wird eine In-Memory-H2-Datenbank verwendet.
 * Das geschieht über das "test"-Profil (application-test.properties).
 */
@DataJpaTest
@ActiveProfiles("test")
class JpaUserRepositoryTest {

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Test
    @DisplayName("save + findByEmail findet gespeicherten Benutzer")
    void saveAndFindByEmail_shouldReturnUser() {
        UserEntityJpa entity = new UserEntityJpa(
                "user-1",
                "marcell",
                "marcell@example.com",
                "hashed-password"
        );

        jpaUserRepository.save(entity);

        Optional<UserEntityJpa> result = jpaUserRepository.findByEmail("marcell@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("marcell");
        assertThat(result.get().getPasswordHash()).isEqualTo("hashed-password");
    }

    @Test
    @DisplayName("save + findByName findet gespeicherten Benutzer")
    void saveAndFindByName_shouldReturnUser() {
        UserEntityJpa entity = new UserEntityJpa(
                "user-2",
                "andrea",
                "andrea@example.com",
                "another-hash"
        );

        jpaUserRepository.save(entity);

        Optional<UserEntityJpa> result = jpaUserRepository.findByName("andrea");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("andrea@example.com");
        assertThat(result.get().getPasswordHash()).isEqualTo("another-hash");
    }
}
