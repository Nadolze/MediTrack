package de.meditrack;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import javax.sql.DataSource;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(classes = MediTrackApplication.class)
class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testDatabaseConnection() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            assertFalse(conn.isClosed(), "Die DB-Verbindung sollte offen sein");
        }
    }
}
