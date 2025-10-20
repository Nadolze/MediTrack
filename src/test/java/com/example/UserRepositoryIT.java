package com.example.repository;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Beispielhafter Integrationstest.
 * Simuliert eine Komponente, die mit einer (fiktiven) Datenbank interagiert.
 */
class UserRepositoryIT {

    @Test
    void shouldSimulateDatabaseInteraction() {
        FakeUserRepository repo = new FakeUserRepository();
        repo.save("Lisa");

        assertTrue(repo.exists("Lisa"), "User should be found after save");
    }

    // Fake-Komponente für Demonstrationszwecke
    static class FakeUserRepository {
        private java.util.Set<String> db = new java.util.HashSet<>();

        void save(String name) {
            db.add(name);
        }

        boolean exists(String name) {
            return db.contains(name);
        }
    }
}
