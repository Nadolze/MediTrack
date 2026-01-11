package com.meditrack.shared.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
/**
 * Smoke-Test für den DatabaseHealthCheck.
 *
 * Ziel:
 * - sicherstellen, dass der HealthCheck als Spring-Bean existiert
 *   und erfolgreich aus dem ApplicationContext geladen werden kann
 * - absichern, dass intern eine datenbanknahe Abhängigkeit
 *   (JdbcTemplate oder DataSource) verwendet wird
 * - frühzeitiges Erkennen von kaputten Konstruktoren oder falscher Konfiguration
 */
@SpringBootTest
class DatabaseHealthCheckTest {

    @Test
    void shouldExistAndBeConstructible(ApplicationContext ctx) {
        DatabaseHealthCheck bean = ctx.getBean(DatabaseHealthCheck.class);
        assertThat(bean).isNotNull();

        boolean hasJdbcOrDataSourceField =
                Arrays.stream(bean.getClass().getDeclaredFields())
                        .anyMatch(f -> f.getType().getName().contains("JdbcTemplate")
                                || f.getType().getName().contains("DataSource"));

        assertThat(hasJdbcOrDataSourceField)
                .as("DatabaseHealthCheck sollte intern JdbcTemplate oder DataSource verwenden")
                .isTrue();
    }
}
