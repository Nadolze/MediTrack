package com.meditrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Zentrale Spring Boot Anwendungsklasse für MediTrack.
 *
 * WICHTIG:
 * --------
 * - Liegt im Package "com.meditrack".
 * - Spring Boot scannt automatisch alle Unterpakete,
 *   also z.B. "com.meditrack.user", "com.meditrack.coredata" usw.
 */
@SpringBootApplication
public class MediTrackApplication {

    /**
     * Einstiegspunkt der Anwendung.
     *
     * @param args Kommandozeilenargumente (werden hier nicht verwendet)
     */
    public static void main(String[] args) {
        SpringApplication.run(MediTrackApplication.class, args);
    }
}
