package com.meditrack.shared.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

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
