package com.meditrack.user.infrastructure.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integrationstest für das JpaUserRepository.
 *
 * Es wird mit einer In-Memory-Datenbank (H2) getestet, ob
 * - ein User gespeichert werden kann
 * - die Methoden findByEmail und findByName funktionieren.
 */
@DataJpaTest
class JpaUserRepositoryTest {

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Test
    @DisplayName("save + findByEmail findet gespeicherten Benutzer")
    void saveAndFindByEmail_shouldReturnUser() {
        UserEntityJpa entity = new UserEntityJpa(
                "user-1",
                "marcell",
                "marcell@example.com"
        );

        jpaUserRepository.save(entity);

        Optional<UserEntityJpa> result = jpaUserRepository.findByEmail("marcell@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("marcell");
    }

    @Test
    @DisplayName("save + findByName findet gespeicherten Benutzer")
    void saveAndFindByName_shouldReturnUser() {
        UserEntityJpa entity = new UserEntityJpa(
                "user-2",
                "andrea",
                "andrea@example.com"
        );

        jpaUserRepository.save(entity);

        Optional<UserEntityJpa> result = jpaUserRepository.findByName("andrea");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("andrea@example.com");
    }
}
