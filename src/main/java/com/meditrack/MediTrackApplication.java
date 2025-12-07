package com.meditrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hauptklasse der Spring-Boot-Anwendung.
 *
 * Diese Klasse dient als Einstiegspunkt (main-Methode),
 * von der aus Spring Boot die gesamte Anwendung startet.
 */
@SpringBootApplication
public class MediTrackApplication {

    public static void main(String[] args) {
        SpringApplication.run(MediTrackApplication.class, args);
    }
}
