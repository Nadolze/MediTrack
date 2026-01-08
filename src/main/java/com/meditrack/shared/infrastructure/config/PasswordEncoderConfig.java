package com.meditrack.shared.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Zentrale Konfiguration für Passwort-Hashing in MediTrack.
 *
 * Dieser Bean wird in allen Bounded Contexts verwendet, die Passwörter
 * verarbeiten (z.B. Benutzerverwaltung).
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * Liefert einen BCrypt-Encoder.
     *
     * Hintergrund:
     * - BCrypt ist ein bewährter Algorithmus für Passwort-Hashing.
     * - Das eigentliche Passwort wird niemals im Klartext gespeichert,
     *   sondern immer nur der erzeugte Hash.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
