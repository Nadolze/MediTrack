package com.meditrack.shared.infrastructure;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class DatabaseCreateTablesTest {

    private EmbeddedDatabase db;

    @AfterEach
    void tearDown() {
        if (db != null) {
            db.shutdown();
        }
    }

    @Test
    void shouldRunWithoutThrowing_onInMemoryH2() {
        db = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName("meditrack-test;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false")
                .build();

        PasswordEncoder encoder = new BCryptPasswordEncoder();

        Environment env = new MockEnvironment()
                .withProperty("meditrack.db.init.enabled", "true")
                .withProperty("meditrack.db.init.drop-and-recreate", "true")
                .withProperty("meditrack.db.init.reset", "true")
                .withProperty("meditrack.db.seed.enabled", "false");

        DatabaseCreateTables sut = new DatabaseCreateTables(db, env, encoder);

        assertThatCode(sut::run).doesNotThrowAnyException();

        // Minimaler Smoke-Check: Schema läuft => zumindest "mt_user" sollte existieren
        JdbcTemplate jdbc = new JdbcTemplate(db);

        // Wenn deine Schema-Skripte andere Tabellennamen haben, hier anpassen
        assertThatCode(() -> jdbc.queryForObject("SELECT COUNT(*) FROM mt_user", Integer.class))
                .doesNotThrowAnyException();
    }
}
