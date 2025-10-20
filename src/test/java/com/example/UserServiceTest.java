package com.example.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Einfacher Beispiel-Unit-Test.
 * Testet eine Methode isoliert (ohne Spring Context oder externe Abhängigkeiten).
 */
class UserServiceTest {

    @Test
    void shouldReturnGreeting() {
        UserService service = new UserService();
        String result = service.greet("Max");
        assertEquals("Hello, Max", result, "Greeting should include the name");
    }

    // Beispielhafte Klasse für den Test (damit er kompiliert)
    static class UserService {
        String greet(String name) {
            return "Hello, " + name;
        }
    }
}
